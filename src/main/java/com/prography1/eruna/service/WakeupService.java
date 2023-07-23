package com.prography1.eruna.service;

import com.prography1.eruna.domain.entity.Alarm;
import com.prography1.eruna.domain.entity.Groups;
import com.prography1.eruna.domain.entity.User;
import com.prography1.eruna.domain.entity.Wakeup;
import com.prography1.eruna.domain.repository.AlarmRepository;
import com.prography1.eruna.domain.repository.GroupRepository;
import com.prography1.eruna.domain.repository.UserRepository;
import com.prography1.eruna.domain.repository.WakeupRepository;
import com.prography1.eruna.response.BaseException;
import com.prography1.eruna.response.BaseResponseStatus;
import com.prography1.eruna.web.UserResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WakeupService {
    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final GroupRepository groupRepository;
    private final WakeupRepository wakeupRepository;
    private final Scheduler scheduler;


    private Wakeup save(UserResDto.WakeupDto wakeupDto, Long groupId){
        User user = userRepository.findByUuid(wakeupDto.getUuid()).orElseThrow(() -> new BaseException(BaseResponseStatus.INVALID_UUID_TOKEN));
        Groups group = groupRepository.findById(groupId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_GROUP));
        Alarm alarm = alarmRepository.findByGroups(group).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_ALARM));

        Wakeup wakeup = Wakeup.builder()
                .alarm(alarm)
                .wakeupCheck(true)
                .date(LocalDate.parse(wakeupDto.getWakeupDate()))
                .wakeupTime(LocalTime.parse(wakeupDto.getWakeupTime()))
                .user(user)
                .build();

        return wakeupRepository.save(wakeup);
    }

    public List<Wakeup> saveAll(List<UserResDto.WakeupDto> list, Long groupId) {
        List<Wakeup> wakeupList = new ArrayList<>();
        log.info("Group " + groupId +  " ALL WAKEUP!!");
        for(UserResDto.WakeupDto wakeupDto : list){
            wakeupList.add(save(wakeupDto, groupId));
            try {
                deleteFcmJob(wakeupDto.getUuid());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

        return wakeupList;
    }

    private void deleteFcmJob(String uuid) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(uuid);
        scheduler.deleteJob(jobKey);
        log.info("fcm job delete : " + uuid);
    }
}
