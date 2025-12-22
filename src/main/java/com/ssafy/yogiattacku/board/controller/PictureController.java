package com.ssafy.yogiattacku.board.controller;

import com.ssafy.yogiattacku.global.response.ResponseBody;
import com.ssafy.yogiattacku.s3.dto.request.PresignPutRequest;
import com.ssafy.yogiattacku.s3.dto.response.PresignPutResponse;
import com.ssafy.yogiattacku.board.service.PictureUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class PictureController {
    private final PictureUploadService uploadService;

    @PostMapping("/{bucketRootKey}/picture/presign")
    public ResponseBody<PresignPutResponse> presign(@PathVariable UUID bucketRootKey, @RequestBody PresignPutRequest request) {
        PresignPutResponse response = uploadService.presignPut(bucketRootKey, request);
        return ResponseBody.success(response);
    }
}
