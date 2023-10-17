<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="uploadFormAction" method="post" enctype="multipart/form-data">
	<input type="file" name="uploadfile" multiple>
	<button type="submit">전송</button>

</form>
<script
  src="https://code.jquery.com/jquery-3.7.1.min.js"
  integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
  crossorigin="anonymous"></script>
<script>
var maxsize= 1024 * 1024 *5;      //byte   5메가바이트
var regex =new RegExp("(.*?)\.(exe|sh|zip|alz)$"); //정규식
function checkExtension(fileName,fileSize){
	if(fileSize >= maxsize){
		alert("파일크기는 5Mbyte 까지만 허용됩니다.");
		return false;
	}
	
	if(regex.test(fileName)){
		alert("실행파일및 압축파일은 업로드가 허용되지 않습니다.")
		return false;
	}
	return true; //정상
}
	$("button").on("click",function(e){
		console.log("버튼클릭");
		e.preventDefault();
		var inputFile=$('input[name="uploadfile"]');
		var file=inputFile[0].files;
		var sumfileSize=0;
		for(var i=0; i<file.length; i++){
			if(!checkExtension(file[i].name,file[i].size)){
				return false; //더이상 진행 금지.
			}
			sumfileSize +=file[i].size;
		}
		if(sumfileSize > 1024*1024*10){
			console.log("전체파일용량",sumfileSize/1024/1024,"메가");
			alert("전체용량 10메가 초과!!!!"+"현재용량은"+ Math.ceil(sumfileSize/1024/1024)+"Mbyte");
			return false;
		}
		$("form").submit();
	});



</script>  
  
</body>
</html>






