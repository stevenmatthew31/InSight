<?php

namespace App\Http\Requests\Auth;

use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rules\Password;

class RegisterRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array
     */
    public function rules()
    {
        return [
                'name' => ['string', 'required', 'max:255'],
                'username' => ['required', 'alpha_num', 'min:3', 'max:25', 'unique:users,username'],
                'email' => ['required', 'email:dns', 'unique:users,email'],
                'password' => ['required', 'confirmed', Password::min(8)->letters()->mixedCase()->numbers()]
        ];
    }
}
