package com.hz.housemonitor.models.hubitat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Devices {
    private List<Device> devicesList;
}
