import axios from "axios";


/**
 * 🚀 Custom Axios Instance
 * 
 * This instance provides a centralized configuration for API calls.
 * - Base URL: `http://localhost:8080/api`
 * - Can be extended with additional headers or interceptors if needed.
 *
 * @constant {AxiosInstance} customAxios
 */
export const customAxios = axios.create({

    // ✅ API base URL
    baseURL: "http://localhost:8080/api",
}) 


// ✅ Debugging log to confirm Axios instance creation
console.log("🔄 Custom Axios instance initialized with baseURL:", customAxios.defaults.baseURL);