from django.shortcuts import render

# Create your views here.


def merry(request):
    someone = ''
    try:
        someone = " to "+request.GET['to']
    except:
        pass

    return render(request, 'merry-christmas.html',{'someone':someone})

def merryy(request):
    return render(request, 'merry-christmas.html',{'someone':' to yy'})
