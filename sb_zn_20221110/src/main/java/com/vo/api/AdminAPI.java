package com.vo.api;

import java.util.List;

import javax.management.MalformedObjectNameException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.vo.conf.NXConf;

/**
 *
 *
 * @author zhangzhen
 * @date 2022年11月15日
 *
 */
@Controller
public class AdminAPI {

	/**
	 * "/" + md5("nxAdmin")
	 */
	public static final String NX_ADMIN = "/51D430BEB02390E20C2D233CBA84DFA0";

	@Autowired
	private NXConf nxConf;

	@GetMapping(value = NX_ADMIN)
	public String admin(final Model model) {
		System.out.println(
				java.time.LocalDateTime.now() + "\t" + Thread.currentThread().getName() + "\t" + "AdminAPI.admin()");

		// FIXME 2022年12月12日 下午11:39:04 zhanghen: 待定，重新看配置文件来返回内容
//		final List<String> url = this.nxConf.getUrl();
//
//		model.addAttribute("balanceStrategy", this.nxConf.getBalanceStrategy());
//		model.addAttribute("url", url);

		return "admin";
	}
}
