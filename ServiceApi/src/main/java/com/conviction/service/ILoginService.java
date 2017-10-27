package com.conviction.service;

public interface ILoginService
{
    boolean login(String name, byte[] encodePassword);
}
