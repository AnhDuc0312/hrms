package com.hrms.sys.services.remote;

import com.hrms.sys.dtos.RemoteDTO;
import com.hrms.sys.models.Remote;

import java.util.List;

public interface IRemoteService {
    public List<Remote> getAllRemotes() throws Exception;

    public Remote getRemoteById(long id) throws Exception;

    public Remote createRemote(RemoteDTO remoteDTO) throws Exception;

    public Remote updateRemote(long id, RemoteDTO remoteDTO) throws Exception;

    public void deleteRemote(long id) throws Exception;

    public void approveRemote(long id) throws Exception;

    public void rejectRemote(long id) throws Exception;
}
