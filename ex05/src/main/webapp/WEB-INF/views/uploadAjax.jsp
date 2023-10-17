<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div class="uploadDiv">
	<input type="file" name="uploadfile"  multiple>
	<button id="uploadBtn">전송</button>
</div>
<ul class="uploadResult">

</ul>
<script
  src="https://code.jquery.com/jquery-3.7.1.min.js"
  integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
  crossorigin="anonymous"></script>
  
<script>
//전체용량이 20Mbyte  이하고,파일갯수가 5개 이하일때만
//업로드 가능(기존조건 +)
//파일용량제한, 파일 확장명 제한 --T/F
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

$(document).ready(function(){
	//최초 파일선택부분 복사(초기화를 위해)
	var cloneObj=$('.uploadDiv');
	
	$(".uploadDiv").on("click","#uploadBtn" ,function(){
		console.log("업로드 클릭!");
		
		
		var formData = new FormData(); //가상 Form
		var inputFile=$('input[name="uploadfile"]');
		console.log("너의 정체는",inputFile);
		
		var file=inputFile[0].files;
		console.log("업로드할 파일정보",file);
		
		//가상 Form에 전송데이터 추가
		var sumfileSize=0;

		if(file.length>5){
			alert("파일이 너무 많습니다. 현재 선택된 파일은"+fileCount+"개");
			return false;
		}
		
		for(var i=0; i<file.length; i++){
			if(!checkExtension(file[i].name,file[i].size)){
				return false; //더이상 진행 금지.
			}		
			formData.append("uploadfile",file[i]);
			sumfileSize +=file[i].size;
		
		}

			
		
		if(sumfileSize > 1024*1024*20){
			alert("전체용량 20메가 초과!!!!"+"현재용량은"+ Math.ceil(sumfileSize/1024/1024)+"Mbyte");
			return false;
		}

		$.ajax({ 
			url:'/uploadAjaxAction' ,
			type: 'post' ,
			data: formData,
			processData: false, //파일전송이 2개 필수로
			contentType: false,
			success:function(result){
				console.log("업로드 결과:",result);
				alert("업로드 성공");
				 //파일선택부분 초기화
				$('.uploadDiv').html(cloneObj.html());
				 //업로드 파일 표시
				 var str="";
				 for(let i=0; i<result.length; i++){
					if(!result[i].isImage){
						 str +=
						 "<a href='/download?fileName="+result[i].uploadPath+"/"+result[i].uuid+"_"+result[i].fileName+"'><li><image src='/resources/file.png'>"+result[i].fileName+"</li></a>";
					}else{ //이미지라면
						fileName=result[i].uploadPath+"/s_"+result[i].uuid;
					 	str +=
						 "<a href='/download?fileName="+result[i].uploadPath+"/"+result[i].uuid+"_"+result[i].fileName+"'><image src='/display?fileName="+fileName+"_"+result[i].fileName+"'>"+result[i].fileName+"</li></a>";
					}
				}
				 $(".uploadResult").append(str);
			} ,
			error:function(){
				console.log("업로드 실패");
			}
			
		}); //ajax
		
		
	});
	


}); //ready
		
		
	


</script>

</body>
</html>





