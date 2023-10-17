package org.zerock.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.doamin.AttachFileDTO;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j
public class UploadController {

	@GetMapping("/uploadForm")
	public void uploadForm() {
		log.info("업로드 페이지 요청했습니다.");
	}
	
	@PostMapping("uploadFormAction")
	public String uploadFormAction(MultipartFile[] uploadfile,Model model) {
		String uploadFolder="c:/upload";
		//해당날짜의 폴더의 만들기
		File uploadPath=new File(uploadFolder,getFolder()); //만들어야할 폴더
		                     //ex>c:/upload/2023/10/13
		if(!uploadPath.exists()) // 폴더가 존재하지 않을때만
			uploadPath.mkdirs(); //디렉토리 만들기
		
		log.info("업로드를 처리합니다.");
		int count=0;
		for(MultipartFile multipartFile : uploadfile  ) {
			log.info("업로드 파일이름:"+multipartFile.getOriginalFilename());
			log.info("업로드 파일사이즈:"+multipartFile.getSize());
			log.info("ContentType"+multipartFile.getContentType());
			log.info("Name:"+multipartFile.getName());
			
			//uuid (고유글자 만들기)
			UUID uuid=UUID.randomUUID();
			String saveFileName=uuid+"_"+multipartFile.getOriginalFilename();
			File saveFile = new File(uploadPath,saveFileName);
			if(checkImageType(saveFile)) {
				//썸네일로 변경
				try {
					FileOutputStream thumbnail=new FileOutputStream(new File(
							uploadPath,"s_"+saveFileName)); //만들 썸네일 파일
					Thumbnailator.createThumbnail(multipartFile.getInputStream(),
							thumbnail,100,100);
					thumbnail.close();
				} catch (Exception e) {
					log.error("썸네일 변경실패");
					e.printStackTrace();
				} 
			}
			try {
				multipartFile.transferTo(saveFile);
			} catch (Exception e) {
				log.error("파일저장 실패");
				e.printStackTrace();
				return "formFail";
			}
		count++;
		}//for
		model.addAttribute("count", count);
		return "formSucess";
		
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		log.info("Ajax로 처리되는 업로드 페이지 요청했습니다.");
	}
	
	 //현재 시점의 년/월/일
	 private String getFolder() {
		 Date date = new Date(); //현재시간 가져오기
		 SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		 String str=sdf.format(date);
		 //str=str.replace("-", File.separator);
		 log.info("만들어진 폴더명:"+str);
		 return str;
	 }
	 
	 //이미지 확인 메소드 리턴 T/F
	 private boolean checkImageType(File file) {
		 boolean isImage=false;
		 try {
			String contentType=Files.probeContentType(file.toPath());
			log.info("업로드된"+file.getName()+"의 타입은:"+contentType);
			
			isImage = contentType.startsWith("image"); //글자의 시작이 image이면 true
		} catch (Exception e) {
			log.warn("알수없는 파일 타입");
			//e.printStackTrace();
		}
		 return isImage;
	 }
	
	 
	@PostMapping(value="uploadAjaxAction")
	@ResponseBody  //데이터를 보내주는 컨트롤러
	public List<AttachFileDTO> uploadAjaxAction(MultipartFile[] uploadfile) {
		List<AttachFileDTO> list = new ArrayList<>();
		String uploadFolder="c:/upload";
		//해당날짜의 폴더의 만들기
		File uploadPath=new File(uploadFolder,getFolder()); //만들어야할 폴더
		                     //ex>c:/upload/2023/10/13
		if(!uploadPath.exists()) // 폴더가 존재하지 않을때만
			uploadPath.mkdirs(); //디렉토리 만들기
		
		log.info("업로드를 처리합니다.");
		int count=0;
		for(MultipartFile multipartFile : uploadfile  ) {
			log.info("업로드 파일이름:"+multipartFile.getOriginalFilename());
			log.info("업로드 파일사이즈:"+multipartFile.getSize());
			log.info("ContentType"+multipartFile.getContentType());
			log.info("Name:"+multipartFile.getName());
			
			//uuid (고유글자 만들기)
			UUID uuid=UUID.randomUUID();
			String saveFileName=uuid+"_"+multipartFile.getOriginalFilename();
			File saveFile = new File(uploadPath,saveFileName);
			boolean isImage=checkImageType(saveFile);
			if(isImage) {
				//썸네일로 변경
				try {
					FileOutputStream thumbnail=new FileOutputStream(new File(
							uploadPath,"s_"+saveFileName)); //만들 썸네일 파일
					Thumbnailator.createThumbnail(multipartFile.getInputStream(),
							thumbnail,100,100);
					thumbnail.close();
				} catch (Exception e) {
					log.error("썸네일 변경실패");
					e.printStackTrace();
				} 
			}
			try {
				multipartFile.transferTo(saveFile);
			} catch (Exception e) {
				log.error("파일저장 실패");
				e.printStackTrace();
				return list;
			}
		count++;
		//응답내용 만들기
		AttachFileDTO dto = new AttachFileDTO(multipartFile.getOriginalFilename(), uploadPath.toString().replace("\\","/"), uuid.toString(), isImage);
		list.add(dto);
		
		}//for
		return list;
		
	}
	
	//파일 데이터 전송(이미지 썸네일용)
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String fileName) {
		ResponseEntity<byte[]> result =null;
		try{
			log.info("보내줘야할 파일이름:"+fileName);
			File file = new File(fileName);
			byte[] sendData="<h1>해킹금지</h1>".getBytes();
			HttpStatus sendStatus=HttpStatus.I_AM_A_TEAPOT;
			String sendContentType="text/html; charset=UTF-8";
			
			if(fileName.substring(0, 10).equals("c:/upload/") 
					&& !fileName.matches(".*/\\.\\./.*") && file.exists()) {
				log.info("정상요청");
				sendData=FileCopyUtils.copyToByteArray(file);
				sendStatus=HttpStatus.OK;
				sendContentType= Files.probeContentType(file.toPath());
			}else {
				log.info("비정상요청");
				//기본값으로 전송
			}
				
			HttpHeaders header = new HttpHeaders();
		
			header.add("Content-Type", sendContentType);
			result=new ResponseEntity<byte[]>(sendData,header,sendStatus);
		
		}catch(Exception e) {
			log.error("오류발생");
		}
		
		return result;
	
	}
	
	//파일 다운로드용
	@GetMapping(value="/download") 
	//produces가 리턴타입이 ResponseEntity고, header가 존재해야지만 강제로 해당값처리
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(String fileName) {
		
		log.info("다운로드할 파일이름:"+fileName);
		
		Resource resource=new FileSystemResource(fileName);
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
		try {
			header.add("Content-Disposition","attachment; filename="
					+new String(resource.getFilename().getBytes("utf-8"),"ISO-8859-1"));
					    //공백 과 한글이름을 올바르게 처리하기 위해서
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//파일이름생성
		return new ResponseEntity<Resource>(
				resource,header,HttpStatus.OK); 
		
		
	}
}














