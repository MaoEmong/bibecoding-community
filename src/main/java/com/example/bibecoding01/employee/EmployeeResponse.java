package com.example.bibecoding01.employee;

import com.example.bibecoding01.user.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeResponse {

    public record DetailDTO(
            Long id,
            String employeeNo,
            String name,
            String email,
            Long departmentId,
            String departmentName,
            Long positionId,
            String positionName,
            EmployeeStatus status,
            LocalDate hiredAt
    ) {
        public static DetailDTO from(User user) {
            return new DetailDTO(
                    user.getId(),
                    user.getEmployeeNo(),
                    user.getName(),
                    user.getEmail(),
                    user.getDepartment().getId(),
                    user.getDepartment().getName(),
                    user.getPosition().getId(),
                    user.getPosition().getName(),
                    user.getStatus(),
                    user.getHiredAt()
            );
        }
    }

    public record ListItemDTO(
            Long id,
            String employeeNo,
            String name,
            String departmentName,
            String positionName,
            EmployeeStatus status
    ) {
        public static ListItemDTO from(User user) {
            return new ListItemDTO(
                    user.getId(),
                    user.getEmployeeNo(),
                    user.getName(),
                    user.getDepartment().getName(),
                    user.getPosition().getName(),
                    user.getStatus()
            );
        }
    }

    public record PageDTO(
            List<ListItemDTO> items,
            int page,
            int size,
            int totalPages,
            long totalCount,
            boolean hasPrev,
            boolean hasNext,
            Integer prevPage,
            Integer nextPage,
            List<Integer> pageNumbers
    ) {
    }

    public static PageDTO pageOf(List<User> users, int page, int size, long totalCount) {
        int totalPages = (int) Math.ceil(totalCount / (double) size);
        List<ListItemDTO> items = users.stream().map(ListItemDTO::from).toList();

        boolean hasPrev = page > 0;
        boolean hasNext = page + 1 < totalPages;

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            pageNumbers.add(i);
        }

        return new PageDTO(
                items,
                page,
                size,
                totalPages,
                totalCount,
                hasPrev,
                hasNext,
                hasPrev ? page - 1 : null,
                hasNext ? page + 1 : null,
                pageNumbers
        );
    }
}
