import React from 'react';
import { useState } from 'react';
import { sendEmail } from '../services/email_service';
import { Editor } from '@tinymce/tinymce-react';
import { useRef } from 'react';


/**
 * 📧 Email Sender Component
 * 
 * This component allows users to input email details and send an email asynchronously.
 * It handles form validation, API requests, and displays alerts for user feedback.
 */
function EmailSender() {

    // ✅ State to store email details
    const [emaildata, setEmaildata] = useState({
        to: "",
        subject: "",
        message: "",
    });


    // ✅ State to track whether email is being sent
    const [sending, setSending] = useState(false);


    // ✅ Load API key from environment variables
    const editorRef = useRef(null);

    // ✅ Create a ref to store editor instance
    const apikey = import.meta.env.VITE_TINY_MCE_API_KEY;



    /**
 * 🔄 Handle Input Field Change
 * 
 * Updates the corresponding field value in the emailData state.
 * 
 * @param {Event} event - The input change event.
 * @param {string} name - The name of the field to update.
 */
    function handleFieldChange(event, name) {
        setEmaildata({
            ...emaildata, [name]: event.target.value
        });


        console.log(`📝 Updated "${name}" field:`, event.target.value); // ✅ Logging for field update
    }

    /**
     * 📤 Handle Form Submission
     * 
     * Validates input fields, sends an email request, and resets the form on success.
     * Displays alerts for errors and provides user feedback.
     * 
     * @param {Event} event - The form submission event.
     */
    async function handlesubmit(event) {

        event.preventDefault();

        // ✅ Validation: Ensure all fields are filled

        if (emaildata.to == "" || emaildata.message == "" || emaildata.subject == "") {

            console.error("❌ Email data is invalid:", emaildata); // ✅ Debugging log

            console.error("email data is Invalid");

            console.log(emaildata);

            // ✅ Show error alert using SweetAlert2
            // eslint-disable-next-line no-undef
            Swal.fire({
                title: "Invalid fields!",
                text: "Please fill in all required fields.",
                icon: "error",
            });

            return;
        }



        try {
            // ✅ Indicate that email is being sent

            setSending(true);

            console.log("🚀 Sending email...", emaildata); // ✅ Debugging log

            // ✅ Make API request

            await sendEmail(emaildata);

            console.log("✅ Email sent successfully:", emaildata); // ✅ Debugging log


            // ✅ Reset form fields after successful send

            setEmaildata(
                {
                    to: "",
                    subject: "",
                    message: "",
                }
            )


            // ✅ Show success alert
            // eslint-disable-next-line no-undef
            Swal.fire({
                title: "Success!",
                text: "Email sent successfully.",
                icon: "success",
            });

        }
        catch (err) {
            console.log(err);

            console.error("❌ Error sending email:", err.message); // ✅ Error logging

        }

        finally {

            // ✅ Reset sending state
            setSending(false);
        }
    }


    return (

        // Main Container
        <div className='min-h-screen w-full border-2 flex justify-center items-center '>


            {/* Email Card Wrapper */}
            <div className='email_card md:w-1/2 w-full border shadow p-4 sm:mx-4 md:mx-0 bg-white dark:bg-gray-700 rounded-lg'>


                <h1 className='text-gray-900 font-bold text-3xl dark:text-white capitalize'>
                    email_card
                </h1>

                <p className='text-gray-700 dark:text-gray-100 capitalize'>
                    send email using your own app...
                </p>


                {/* form start */}

                <form onSubmit={handlesubmit}>

                    {/* Email Input Field */}
                    <div className="mb-5">


                        <label htmlFor="base-input"
                            className="capitalize block mb-2 text-sm font-medium text-gray-900 dark:text-neutral-50">

                            to

                        </label>


                        <input
                            value={emaildata.to}
                            onChange={(e) => handleFieldChange(e, "to")}
                            type="text" id="base-input" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg
             focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700
             dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500
             dark:focus:border-blue-500"
                            placeholder='enter email'
                        />


                    </div>


                    {/* Subject Input Field */}

                    <div className="mb-5">


                        <label htmlFor="base-input"
                            className="capitalize block mb-2 text-sm font-medium text-gray-900 dark:text-neutral-50">

                            subject

                        </label>


                        <input
                            value={emaildata.subject}

                            onChange={(e) => handleFieldChange(e, "subject")}
                            type="text" id="base-input" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg
                  focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700
                  dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500
                  dark:focus:border-blue-500"
                            placeholder='enter email'
                        />


                    </div>


                    {/* Message Textarea */}


                    {/* 
                    
                                        <div className='mb-4'>

                        <label htmlFor="message"
                            className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Your
                            message</label>
                        <textarea
                            value={emaildata.message}
                            onChange={(e) => handleFieldChange(e, "message")}

                            id="message" rows="4"
                            className="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                            placeholder="Write your thoughts here..."></textarea>

                    </div>

                    */}

                    {/* Message Textarea */}

                    <div className='mt-3 mb-3'>



                        <label className='capitalize block mb-2 text-sm font-medium text-gray-900 dark:text-neutral-50'>your message</label>


                        <Editor

                            /**
                             * 📝 Handles editor content change
                             * Updates the email message state dynamically
                             */

                            onEditorChange={(event) => {

                                console.log("✏️ Editor content updated!");
                                console.log(event);


                                setEmaildata({ ...emaildata, "message": editorRef.current.getContent() });
                            }}

                            /**
 * 🚀 Initializes the TinyMCE editor instance
 * Stores reference to editor for future use
 */

                            apiKey={apikey}
                            onInit={(_evt, editor) => {
                                console.log("✅ TinyMCE Editor Initialized!");
                                editorRef.current = editor;
                            }}


                            /**
 * ⚙️ TinyMCE Configuration Settings
 * Defines plugins, toolbar buttons, and UI behavior
 */

                            init={{
                                plugins: [
                                    // Core editing features
                                    'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'image', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
                                    // Your account includes a free trial of TinyMCE premium features
                                    // Try the most popular premium features until May 26, 2025:
                                    'checklist', 'mediaembed', 'casechange', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'editimage', 'advtemplate', 'ai', 'mentions', 'tinycomments', 'tableofcontents', 'footnotes', 'mergetags', 'autocorrect', 'typography', 'inlinecss', 'markdown', 'importword', 'exportword', 'exportpdf'
                                ],
                                toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
                                tinycomments_mode: 'embedded',
                                tinycomments_author: 'Author name',
                                mergetags_list: [
                                    { value: 'First.Name', title: 'First Name' },
                                    { value: 'Email', title: 'Email' },
                                ],

                                /**
       * 🤖 Handles AI requests (Disabled by default)
       * Replace with API implementation if needed
       */
                                ai_request: (request, respondWith) => respondWith.string(() => Promise.reject('See docs to implement AI Assistant')),
                            }}

                            initialValue="Write your message here"

                        />

                    </div>


                    {/* spinner */}

                    {/* only visible when sending email */}
                    {sending && <div className='loader flex flex-col gap-2 mt-4 mb-4 justify-center items-center'>



                        <div role="status">


                            <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor" />
                                <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill" />
                            </svg>
                            <span className="sr-only">Loading...</span>




                        </div>



                        <p className='text-xl capitalize antialiased font-bold dark:text-white'>
                            sending email....
                        </p>




                    </div>
                    }







                    {/* Action Buttons (Send & Clear) */}

                    <div className='button-container mb-4 flex justify-center gap-3'>

                        {/* send button is disabled when sending email */}

                        <button type="submit"

                            disabled={sending}
                            className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300
          font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 
          focus:outline-none dark:focus:ring-blue-800 capitalize">

                            send email

                        </button>

                        {/* clear button is disabled when sending email */}

                        <button type="reset"
                            disabled={sending}
                            className="text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:ring-red-300
          font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 
          focus:outline-none dark:focus:ring-red-800 capitalize">

                            clear

                        </button>

                    </div>


                </form>

            </div>

        </div>



    )
}

export default EmailSender