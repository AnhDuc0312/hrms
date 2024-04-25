package com.hrms.sys.services.remote;

import com.hrms.sys.dtos.RemoteDTO;
import com.hrms.sys.models.Remote;
import com.hrms.sys.repositories.RemoteRepository;
import com.hrms.sys.services.remote.IRemoteService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RemoteService implements IRemoteService {

    private final RemoteRepository remoteRepository;

    private final ModelMapper modelMapper;


    @Override
    public List<Remote> getAllRemotes() throws Exception {
        return remoteRepository.findAll();
    }

    @Override
    public Remote getRemoteById(long id) throws Exception {
        return remoteRepository.findById(id).orElse(null);
    }

    @Override
    public Remote createRemote(RemoteDTO remoteDTO) throws Exception {
        Remote remote = modelMapper.map(remoteDTO, Remote.class);

        return remoteRepository.save(remote);
    }

    @Override
    public Remote updateRemote(long id, RemoteDTO remoteDTO) throws Exception {
        Remote existingRemote = remoteRepository.findById(id).orElse(null);
        if (existingRemote == null) {
            return null; // hoặc throw exception tùy theo yêu cầu
        }
        modelMapper.map(remoteDTO, existingRemote); // Cập nhật thông tin từ DTO vào đối tượng Remote hiện tại
        // Cập nhật các trường khác nếu cần thiết
        return remoteRepository.save(existingRemote);
    }

    @Override
    public void deleteRemote(long id) throws Exception {
        remoteRepository.deleteById(id);
    }

    @Override
    public void approveRemote(long id) throws Exception {
        Remote remote = remoteRepository.findById(id).orElse(null);

    }

    @Override
    public void rejectRemote(long id) throws Exception{
        Remote remote = remoteRepository.findById(id).orElse(null);

    }
}
