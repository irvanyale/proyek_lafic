package com.proyekta.app.project_lafic.helper;

import com.proyekta.app.project_lafic.model.KategoriBarang;
import com.proyekta.app.project_lafic.model.Member;

import java.util.List;
import java.util.Vector;

/**
 * Created by WINDOWS 10 on 16/06/2017.
 */

public class MemberHelper {

    private static List<Member> member;

    public static List<Member> getMember(){
        if (member == null){
            member = new Vector<>();
        }
        return member;
    }

}
