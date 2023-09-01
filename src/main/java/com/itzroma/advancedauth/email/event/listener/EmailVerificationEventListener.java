package com.itzroma.advancedauth.email.event.listener;

import com.itzroma.advancedauth.email.EmailSender;
import com.itzroma.advancedauth.email.event.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationEventListener {
    private final EmailSender emailSender;

    @EventListener
    public void onApplicationEvent(EmailVerificationEvent event) {
        String url = event.getUrl() + "/verify?token=" + event.getEmailVerificationToken().getToken();
        String userEmail = event.getUser().getEmail();

        // TODO: 8/25/2023 think how to replace hardcoded value
        long expirationMinutes = 600_000 / 1000 / 60;
        emailSender.send(userEmail, buildEmail(userEmail, url, expirationMinutes));
    }

    private String buildEmail(String name, String link, long expirationMinutes) {
        String emailText = """
                <div style="font-family: Helvetica, Arial, sans-serif; font-size: 16px; margin: 0; color: #0b0c0c;">
                    <table role="presentation" width="100%" style="border-collapse: collapse; min-width: 100%; width: 100% !important;" cellpadding="0" cellspacing="0" border="0">
                        <tr>
                            <td width="100%" height="53" bgcolor="#0b0c0c">
                                <table role="presentation" width="100%" style="border-collapse: collapse; max-width: 580px" cellpadding="0" cellspacing="0" border="0" align="center">
                                    <tr>
                                        <td width="70" bgcolor="#0b0c0c" valign="middle">
                                            <table role="presentation" cellpadding="0" cellspacing="0" border="0" style="border-collapse: collapse">
                                                <tr>
                                                    <td style="padding-left: 10px"></td>
                                                    <td style="font-size: 28px; line-height: 1.315789474; margin-top: 4px; padding-left: 10px;">
                                                        <span style="font-family: Helvetica, Arial, sans-serif; font-weight: 700; color: #ffffff; text-decoration: none; vertical-align: top; display: inline-block;">Confirm your email</span>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    <table role="presentation" class="m_-6186904992287805515content" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse: collapse; max-width: 580px; width: 100% !important;" width="100%">
                        <tr>
                            <td width="10" height="10" valign="middle"></td>
                            <td>
                                <table role="presentation" width="100%" cellpadding="0" cellspacing="0" border="0" style="border-collapse: collapse">
                                    <tr>
                                        <td bgcolor="#1D70B8" width="100%" height="10"></td>
                                    </tr>
                                </table>
                            </td>
                            <td width="10" valign="middle" height="10"></td>
                        </tr>
                    </table>
                    <table role="presentation" class="m_-6186904992287805515content" align="center" cellpadding="0" cellspacing="0" border="0" style="border-collapse: collapse; max-width: 580px; width: 100% !important;" width="100%">
                        <tr>
                            <td height="30"><br /></td>
                        </tr>
                        <tr>
                            <td width="10" valign="middle"><br /></td>
                            <td style="font-family: Helvetica, Arial, sans-serif; font-size: 19px; line-height: 1.315789474; max-width: 560px;">
                                <p style="margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c;">Hi [name-placeholder]</p>
                                <p style="margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c;">Thank you for registering. Please click on the below link to activate your account:</p>
                                <blockquote style="margin: 0 0 20px 0; border-left: 10px solid #b1b4b6; padding: 15px 0 0.1px 15px; font-size: 19px; line-height: 25px;">
                                    <p style="margin: 0 0 20px 0; font-size: 19px; line-height: 25px; color: #0b0c0c;">
                                        <a target="_blank" href="[activation-link-placeholder]">Activate Now</a>
                                    </p>
                                </blockquote>
                                Link will expire in [expiration-minutes-placeholder] minutes.
                                <p>See you soon</p>
                            </td>
                            <td width="10" valign="middle"><br /></td>
                        </tr>
                        <tr>
                            <td height="30"><br /></td>
                        </tr>
                    </table>
                    <div class="yj6qo"></div>
                    <div class="adL"></div>
                </div>""";
        emailText = emailText.replace("[name-placeholder]", name);
        emailText = emailText.replace("[activation-link-placeholder]", link);
        emailText = emailText.replace("[expiration-minutes-placeholder]", String.valueOf(expirationMinutes));
        return emailText;
    }
}
