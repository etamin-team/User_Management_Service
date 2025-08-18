package com.example.user_management_service.service.v2;

import com.example.user_management_service.model.MNN;
import com.example.user_management_service.model.Medicine;
import com.example.user_management_service.model.dto.MNNDto;
import com.example.user_management_service.repository.MNNRepository;
import com.example.user_management_service.repository.MedicineRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Date-8/18/2025
 * By Sardor Tokhirov
 * Time-5:42 PM (GMT+5)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DataBaseServiceV2 {


    private final MNNRepository mnnRepository;
    private final MedicineRepository medicineRepository;

    public List<MNNDto> parseFileMNN(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(
                file.getInputStream(),
                new TypeReference<List<MNNDto>>() {
                }
        );
    }

    public MNN saveMNN(MNNDto mnnDto) {
        MNN mnn = mnnDtoConverter(mnnDto);
        if (mnn.getId() == null || mnn.getId() == 0) {
            Long maxId = mnnRepository.findMaxId().orElse(0L);
            mnn.setId(maxId + 1);
        }

        List<Medicine>  medicines=medicineRepository.findByIds(mnnDto.getMedicineIds());
        for (Medicine medicine : medicines) {
            List<MNN> mnns=medicine.getMnn();
            mnns.add(mnnRepository.save(mnn));
            medicine.setMnn(mnns);
            medicineRepository.save(medicine);
        }
        return mnn;
    }
    private MNN mnnDtoConverter(MNNDto mnn) {
        MNN mnn1 = new MNN();
        mnn1.setId(mnn.getId());
        mnn1.setName(mnn.getName());
        mnn1.setType(mnn.getType());
        mnn1.setDosage(mnn.getDosage());
        mnn1.setCombination(mnn.getCombination());
        mnn1.setLatinName(mnn.getLatinName());
        mnn1.setPharmacotherapeuticGroup(mnn.getPharmacotherapeuticGroup());
        mnn1.setWm_ru(mnn.getWm_ru());
        return mnn1;
    }
    public Map<Long, MNNDto> saveMNNList(List<MNNDto> mnns) {
        Map<Long, MNNDto> errors = new HashMap<>();

        System.out.println("in process------------------------------");
        for (MNNDto mnn : mnns) {
            try {
                saveMNN(mnn);
            } catch (Exception e) {
                errors.put(mnn.getId(), mnn);
            }
        }
        System.out.println("Done Saving -------------------------------------");

        return errors;
    }
}
