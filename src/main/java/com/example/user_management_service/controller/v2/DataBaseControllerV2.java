package com.example.user_management_service.controller.v2;

import com.example.user_management_service.model.MNN;
import com.example.user_management_service.model.dto.MNNDto;
import com.example.user_management_service.service.DataBaseService;
import com.example.user_management_service.service.v2.DataBaseServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Date-8/18/2025
 * By Sardor Tokhirov
 * Time-5:39 PM (GMT+5)
 */
@RestController
@RequestMapping("/api/v2/db")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class DataBaseControllerV2 {
    private final DataBaseServiceV2 dataBaseServiceV2;
    @PostMapping("/mnn/add")
    public ResponseEntity<MNN> saveMNN(@RequestBody MNNDto mnnDto) {
        MNN mnn1 = dataBaseServiceV2.saveMNN(mnnDto);
        return ResponseEntity.ok(mnn1);
    }
    @PostMapping(value = "/mnn/add-bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<Long, MNNDto>> uploadMNN(@RequestParam("file") MultipartFile file) throws IOException {
        List<MNNDto> mnnList=dataBaseServiceV2.parseFileMNN(file);
        Map<Long, MNNDto> err=dataBaseServiceV2.saveMNNList(mnnList);
        return ResponseEntity.ok(err);
    }



}
