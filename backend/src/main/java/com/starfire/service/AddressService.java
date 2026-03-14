package com.starfire.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starfire.entity.Address;
import com.starfire.mapper.AddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    
    private final AddressMapper addressMapper;
    
    public AddressService(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }
    
    // 获取用户所有收货地址（私有 - 仅用户自己可见）
    public List<Address> getUserAddresses(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("is_default", "create_time");
        return addressMapper.selectList(wrapper);
    }
    
    // 获取用户默认收货地址
    public Address getDefaultAddress(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_default", 1);
        return addressMapper.selectOne(wrapper);
    }
    
    // 获取地址详情
    public Address getAddressById(Long addressId, Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", addressId);
        wrapper.eq("user_id", userId);
        return addressMapper.selectOne(wrapper);
    }
    
    // 添加收货地址
    @Transactional
    public boolean addAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddresses(address.getUserId());
        }
        
        // 如果没有默认地址，设为第一个
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", address.getUserId());
        Long count = addressMapper.selectCount(wrapper);
        if (count == 0) {
            address.setIsDefault(1);
        }
        
        return addressMapper.insert(address) > 0;
    }
    
    // 更新收货地址
    @Transactional
    public boolean updateAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddresses(address.getUserId());
        }
        
        return addressMapper.updateById(address) > 0;
    }
    
    // 删除收货地址
    public boolean deleteAddress(Long addressId, Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("id", addressId);
        wrapper.eq("user_id", userId);
        
        Address address = addressMapper.selectOne(wrapper);
        if (address == null) {
            return false;
        }
        
        boolean deleted = addressMapper.delete(wrapper) > 0;
        
        // 如果删除的是默认地址，设为其他地址为默认
        if (deleted && address.getIsDefault() == 1) {
            QueryWrapper<Address> remainingWrapper = new QueryWrapper<>();
            remainingWrapper.eq("user_id", userId);
            remainingWrapper.last("LIMIT 1");
            Address newDefault = addressMapper.selectOne(remainingWrapper);
            if (newDefault != null) {
                newDefault.setIsDefault(1);
                addressMapper.updateById(newDefault);
            }
        }
        
        return deleted;
    }
    
    // 设为默认地址
    @Transactional
    public boolean setDefaultAddress(Long addressId, Long userId) {
        // 清除其他默认地址
        clearDefaultAddresses(userId);
        
        // 设为默认
        Address address = getAddressById(addressId, userId);
        if (address == null) {
            return false;
        }
        
        address.setIsDefault(1);
        return addressMapper.updateById(address) > 0;
    }
    
    // 清除用户所有默认地址
    private void clearDefaultAddresses(Long userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_default", 1);
        
        List<Address> defaultAddresses = addressMapper.selectList(wrapper);
        for (Address address : defaultAddresses) {
            address.setIsDefault(0);
            addressMapper.updateById(address);
        }
    }
}
