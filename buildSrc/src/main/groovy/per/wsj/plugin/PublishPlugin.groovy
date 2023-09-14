package per.wsj.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import per.wsj.plugin.bean.PublishConfig

class PublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("------------plugin------------")
        def android = project.extensions.getByType(AppExtension)
        // 创建属性，之后才能在apply的地方设置uploadnotice属性
        PublishConfig config = project.getExtensions().create("publishConfig", PublishConfig.class)

        PublishTask publishTask

        project.afterEvaluate {
            publishTask = project.getTasks().create("publish_fengyun", PublishTask.class)
        }

        project.gradle.buildFinished {
            android.applicationVariants.all { variant ->
                if (variant.buildType.name == "release") {
                    println("-----------------run every time when release--------------------")
//                    publishTask.run()
                }
            }
        }

        /*applicationVariants.all { variant ->
        variant.outputs.all { output ->
            *//*output.processResources.doFirst { pm ->
                String manifestPath = output.processResources.manifestFile
                println "=====test manifestPath=====$manifestPath"
                def manifestContent = file(manifestPath).getText()
                def xml = new XmlParser().parseText(manifestContent)
//                println "=====test xml=====$xml"
                // 增加一个meta-data节点
//                xml.application[0].appendNode("meta-data", ['android:name': 'channel', 'android:value': 'yingyongbao'])
                // 增加一个属性
//                xml.application[0].attributes().put("android:persistent", "true")

                def serialize = groovy.xml.XmlUtil.serialize(xml)
                file(manifestPath).write(serialize)
            }*//*
            if (variant.buildType.name == 'release') {
                def fileName = "fengyun-weather-" + defaultConfig.versionName + ".apk"
                outputFileName = new File("", fileName)
            } else if (variant.buildType.name == 'debug') {
//                fileName = "fengyun-weather-debug-" + ".apk"
            }
        }
    }*/

        /*gradle.buildFinished { var1 ->
            println var1.getAction()
            println '构建结束 buildFinished'
        }*/
    }
}