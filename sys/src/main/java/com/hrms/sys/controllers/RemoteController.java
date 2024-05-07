package com.hrms.sys.controllers;

import com.hrms.sys.dtos.RemoteDTO;
import com.hrms.sys.models.Remote;
import com.hrms.sys.services.remote.RemoteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("${api.prefix}/remotes")
public class RemoteController {
    private final RemoteService remoteService;


    @GetMapping
    public ResponseEntity<List<Remote>> getAllRemotes() {
        try {
            List<Remote> remotes = remoteService.getAllRemotes();
            return ResponseEntity.ok(remotes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Remote> getRemoteById(@PathVariable("id") long id) {
        try {
            Remote remote = remoteService.getRemoteById(id);
            if (remote != null) {
                return ResponseEntity.ok(remote);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<Remote> createRemote(@RequestBody RemoteDTO remoteDTO) {
        try {
            Remote createdRemote = remoteService.createRemote(remoteDTO);
            return ResponseEntity.ok(createdRemote);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Remote> updateRemote(@PathVariable("id") long id, @RequestBody RemoteDTO remoteDTO) {
        try {
            Remote updatedRemote = remoteService.updateRemote(id, remoteDTO);
            if (updatedRemote != null) {
                return ResponseEntity.ok(updatedRemote);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRemote(@PathVariable("id") long id) {
        try {
            remoteService.deleteRemote(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveRemote(@PathVariable("id") long id) {
        try {
            remoteService.approveRemote(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRemote(@PathVariable("id") long id) {
        try {
            remoteService.rejectRemote(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
