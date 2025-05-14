import { customAxios } from './helper.js'


/**
 * 📧 Send Email Function
 *
 * This function sends an email using Axios and handles errors properly.
 * It logs the request and response details for better debugging.
 *
 * @param {Object} emailData - The email payload to send (e.g., { to, subject, message }).
 * @returns {Object | null} - The response data from the server or `null` on error.
 */
export async function sendEmail(emaildata) {
    try {

        console.log("📤 Sending email with data:", emaildata);// ✅ Log request data

        let response = await customAxios.post("/send", emaildata);

        console.log("✅ Email sent successfully:", response.data); // ✅ Log response data


        let data = response.data;

        return data; // Return the parsed response
    } catch (error) {

        // ✅ Log detailed error information for debugging

        console.error("❌ Error sending email:", error.message);
        console.error("⚠️ Error Details:", error.response?.data || "No additional details available");
        
        return null; // Return null in case of error

    }
}