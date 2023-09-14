package per.wsj.plugin

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.event.ProgressEvent
import com.aliyun.oss.event.ProgressEventType
import com.aliyun.oss.event.ProgressListener
import com.aliyun.oss.model.PutObjectRequest
import com.android.build.gradle.AppExtension
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import per.wsj.plugin.bean.PublishBean
import per.wsj.plugin.bean.PublishConfig

class PublishTask extends DefaultTask {

    // 版本号
    def versionCode
    // 版本名
    def versionName

    def publishConfig

    PublishTask() {
        setGroup("publish_fengyun")
        setDescription("上传apk到云存储，并提交到服务端")
        def android = project.extensions.getByType(AppExtension)
        versionCode = android.defaultConfig.versionCode
        versionName = android.defaultConfig.versionName
        publishConfig = getProject().getExtensions().getByType(PublishConfig.class)

//        Properties properties = new Properties()
//        FileInputStream is = new FileInputStream(project.rootDir.getAbsolutePath() + "/config.properties")
//        properties.load(is)
//        publishConfig.versionDesc = new String(properties.getProperty("versionDesc").toString().getBytes(), "UTF-8")
        println(publishConfig.versionDesc)
    }

    @TaskAction
    void run() {
        println("---------------begin publish------------------")
        def apkName = "fengyun-weather-" + versionName + ".apk"
        def apkFile = new File(publishConfig.apkPath)
        def newPath = apkFile.getParentFile().getAbsolutePath() + File.separator + apkName
        // 重命名
        apkFile.renameTo(newPath)
        // 改为新路径
        publishConfig.apkPath = newPath
        upload(apkName)
    }

    /**
     * 上传apk
     * @param apkPath
     * @return
     */
    def upload(String apkName) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = publishConfig.ossEndpoint
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = publishConfig.ossAkId
        String accessKeySecret = publishConfig.ossAkSecret
        String bucketName = "wangsj"
        String objectName = "fengyun/" + apkName

        long bytesWritten = 0
        long totalBytes = -1

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)

        def listener = new ProgressListener() {
            @Override
            void progressChanged(ProgressEvent progressEvent) {
                long bytes = progressEvent.getBytes()
                ProgressEventType eventType = progressEvent.getEventType()
                switch (eventType) {
                    case ProgressEventType.TRANSFER_STARTED_EVENT:
                        System.out.println("Start to upload......")
                        break
                    case ProgressEventType.REQUEST_CONTENT_LENGTH_EVENT:
                        totalBytes = bytes
                        System.out.println(totalBytes + " bytes in total will be uploaded to OSS")
                        break
                    case ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT:
                        bytesWritten += bytes
                        if (totalBytes != -1) {
                            int percent = (int) (bytesWritten * 100.0 / totalBytes)
                            System.out.println(bytes + " bytes have been written at this time, upload progress: " + percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")")
                        } else {
                            System.out.println(bytes + " bytes have been written at this time, upload ratio: unknown" + "(" + this.bytesWritten + "/...)")
                        }
                        break
                    case ProgressEventType.TRANSFER_COMPLETED_EVENT:
                        System.out.println("Succeed to upload, " + bytesWritten + " bytes have been transferred in total")
                        publish(bucketName, endpoint, objectName)
                        break
                    case ProgressEventType.TRANSFER_FAILED_EVENT:
                        System.out.println("Failed to upload, " + bytesWritten + " bytes have been transferred")
                        break
                    default:
                        break
                }
            }
        }

        try {
            // 带进度条的上传。
            ossClient.putObject(new PutObjectRequest(bucketName, objectName, new File(publishConfig.apkPath)).
                    <PutObjectRequest> withProgressListener(listener))
        } catch (Exception e) {
            e.printStackTrace()
        }
        // 关闭OSSClient。
        ossClient.shutdown()
    }

    /**
     * 上传成功后请求服务器
     */
    def publish(bucketName, endpoint, fileName) {
        def fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName
        def conn = new URL(publishConfig.publishUrl).openConnection()
        conn.setRequestMethod('POST')
//        conn.setRequestProperty("Connection", "Keep-Alive")
        conn.setRequestProperty("Content-type", "application/json;charset=UTF-8")
        conn.setConnectTimeout(30000)
        conn.setReadTimeout(30000)
        conn.setDoInput(true)
        conn.setDoOutput(true)
        def dos = new DataOutputStream(conn.getOutputStream())
        PublishBean publishBean = new PublishBean()
        publishBean.version_code = versionCode
        publishBean.version_name = versionName
        publishBean.url_full = fileUrl
        publishBean.url_part = ""
        publishBean.content = publishConfig.versionDesc
        def json = JsonOutput.toJson(publishBean)
        println(json)
        dos.writeBytes(json)
        def input = new BufferedReader(new InputStreamReader(conn.getInputStream()))
        String line = ""
        String result = ""
        while ((line = input.readLine()) != null) {
            result += line
        }
        dos.flush()
        dos.close()
        input.close()
        conn.connect()
        println(result)
    }
}