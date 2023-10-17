package org.zerock.doamin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachFileDTO {
	//파일이름
	private String fileName;
	//업로드경로
	private String uploadPath;
	//uuid
	private String uuid;
	//이미지여부
	private boolean isImage;
}
