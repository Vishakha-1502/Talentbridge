'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { setCookie } from 'cookies-next';
import {
  User,
  Building2,
  Mail,
  Lock,
  Eye,
  EyeOff,
  ArrowRight,
  CheckCircle2,
  AlertCircle,
  Briefcase,
} from 'lucide-react';

export default function LoginPage() {
  const [selectedRole, setSelectedRole] = useState('jobseeker'); // 'jobseeker' | 'recruiter'
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [roleMismatch, setRoleMismatch] = useState(null);
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
    setError('');
    setRoleMismatch(null);
  };

  const handleRoleChange = (role) => {
    setSelectedRole(role);
    setError('');
    setRoleMismatch(null);
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setRoleMismatch(null);
    setLoading(true);

    if (!formData.email || !formData.password) {
      setError('Please fill in all fields');
      setLoading(false);
      return;
    }

    if (!/\S+@\S+\.\S+/.test(formData.email)) {
      setError('Please enter a valid email address');
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post(
        `${process.env.NEXT_PUBLIC_API_URL}/login`,
        {
          email: formData.email,
          password: formData.password,
        }
      );

      const decodedToken = jwtDecode(response.data);
      const actualRole = (decodedToken.role || '').toLowerCase();
      const chosenRole = selectedRole.toLowerCase();

      // Check for role mismatch
      if (actualRole && actualRole !== chosenRole) {
        const friendlyActual = actualRole === 'recruiter' ? 'Recruiter' : 'Job Seeker';
        const friendlyChosen = chosenRole === 'recruiter' ? 'Recruiter' : 'Job Seeker';

        setRoleMismatch({
          actualRole,
          friendlyActual,
          friendlyChosen,
          tokenData: response.data,
          decodedToken,
        });
        setError(
          `This account is registered as a ${friendlyActual}, but you selected ${friendlyChosen}.`
        );
        setLoading(false);
        return;
      }

      // Role matched: proceed with login
      completeLogin(response.data, decodedToken, actualRole);
    } catch (err) {
      console.error('Login error:', err);
      if (err.response?.data) {
        const errorMessage =
          err.response.data.message || err.response.data.error || 'Invalid email or password';
        setError(errorMessage);
      } else if (err.request) {
        setError('Network error. Please check your connection and try again.');
      } else {
        setError('Something went wrong. Please try again.');
      }
      setLoading(false);
    }
  };

  const completeLogin = (token, decodedToken, roleToUse) => {
    localStorage.setItem('token', token);
    localStorage.setItem('email', decodedToken.sub);
    if (decodedToken.user_id) {
      localStorage.setItem('userId', decodedToken.user_id);
    }
    setCookie('role', roleToUse, { maxAge: 60 * 60 * 24 * 7 });
    setCookie('userRole', roleToUse, { maxAge: 60 * 60 * 24 * 7 });
    setCookie('email', decodedToken.sub, { maxAge: 60 * 60 * 24 * 7 });

    setSuccess(
      `Login successful as ${roleToUse === 'recruiter' ? 'Recruiter' : 'Job Seeker'}! Redirecting...`
    );

    setTimeout(() => {
      if (roleToUse === 'jobseeker') {
        router.push('/jobseeker/dashboard');
      } else if (roleToUse === 'recruiter') {
        router.push('/recruiter/dashboard');
      } else {
        router.push('/');
      }
    }, 900);
  };

  const handleProceedWithActualRole = () => {
    if (roleMismatch) {
      setSelectedRole(roleMismatch.actualRole);
      setError('');
      setLoading(true);
      completeLogin(
        roleMismatch.tokenData,
        roleMismatch.decodedToken,
        roleMismatch.actualRole
      );
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-50/50 via-gray-50 to-purple-50/40 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8">
      {/* Brand Header */}
      <div className="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <Link href="/" className="inline-flex items-center gap-2 mb-2">
          <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-blue-500/20 font-extrabold text-xl">
            TB
          </div>
          <span className="text-2xl font-black bg-gradient-to-r from-gray-900 to-gray-700 bg-clip-text text-transparent">
            TalentBridge
          </span>
        </Link>
        <h2 className="mt-2 text-2xl font-extrabold text-gray-900 tracking-tight sm:text-3xl">
          Sign In to Your Account
        </h2>
        <p className="mt-1.5 text-sm text-gray-600">
          Choose whether you are signing in to look for jobs or hire candidates
        </p>
      </div>

      <div className="mt-6 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-5 sm:px-10 shadow-xl shadow-gray-200/60 rounded-3xl border border-gray-100">
          {/* Two Options: Job Seeker or Recruiter */}
          <div className="mb-6">
            <label className="block text-xs font-bold text-gray-500 uppercase tracking-wider mb-2.5">
              Select Your Role
            </label>
            <div className="grid grid-cols-2 gap-3">
              {/* Option 1: Job Seeker */}
              <button
                type="button"
                onClick={() => handleRoleChange('jobseeker')}
                className={`relative flex flex-col items-center justify-center p-4 rounded-2xl border-2 transition-all duration-200 cursor-pointer ${
                  selectedRole === 'jobseeker'
                    ? 'border-blue-600 bg-blue-50/70 text-blue-900 shadow-md shadow-blue-100'
                    : 'border-gray-200 bg-white text-gray-600 hover:border-gray-300 hover:bg-gray-50'
                }`}
              >
                <div
                  className={`p-2.5 rounded-xl mb-2 transition-colors ${
                    selectedRole === 'jobseeker'
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-500'
                  }`}
                >
                  <User className="w-5 h-5" />
                </div>
                <span className="font-bold text-sm">Job Seeker</span>
                <span className="text-[11px] text-gray-500 mt-0.5">Find & Apply</span>

                {selectedRole === 'jobseeker' && (
                  <span className="absolute top-2 right-2 flex h-2 w-2">
                    <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
                    <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-600"></span>
                  </span>
                )}
              </button>

              {/* Option 2: Recruiter */}
              <button
                type="button"
                onClick={() => handleRoleChange('recruiter')}
                className={`relative flex flex-col items-center justify-center p-4 rounded-2xl border-2 transition-all duration-200 cursor-pointer ${
                  selectedRole === 'recruiter'
                    ? 'border-indigo-600 bg-indigo-50/70 text-indigo-900 shadow-md shadow-indigo-100'
                    : 'border-gray-200 bg-white text-gray-600 hover:border-gray-300 hover:bg-gray-50'
                }`}
              >
                <div
                  className={`p-2.5 rounded-xl mb-2 transition-colors ${
                    selectedRole === 'recruiter'
                      ? 'bg-indigo-600 text-white'
                      : 'bg-gray-100 text-gray-500'
                  }`}
                >
                  <Building2 className="w-5 h-5" />
                </div>
                <span className="font-bold text-sm">Recruiter</span>
                <span className="text-[11px] text-gray-500 mt-0.5">Post Jobs & Hire</span>

                {selectedRole === 'recruiter' && (
                  <span className="absolute top-2 right-2 flex h-2 w-2">
                    <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-indigo-400 opacity-75"></span>
                    <span className="relative inline-flex rounded-full h-2 w-2 bg-indigo-600"></span>
                  </span>
                )}
              </button>
            </div>

            {/* Role Helper Banner */}
            <div className="mt-3 text-xs flex items-center gap-1.5 text-gray-500 bg-gray-50 px-3 py-2 rounded-xl border border-gray-100">
              <Briefcase className="w-3.5 h-3.5 text-gray-400 flex-shrink-0" />
              <span>
                Logging in to{' '}
                <strong className="text-gray-800">
                  {selectedRole === 'jobseeker'
                    ? 'Job Seeker Dashboard'
                    : 'Recruiter Management Portal'}
                </strong>
              </span>
            </div>
          </div>

          {/* Error Display */}
          {error && (
            <div className="mb-5 p-4 bg-red-50 border border-red-200 rounded-2xl text-red-800 text-sm">
              <div className="flex items-start gap-2.5">
                <AlertCircle className="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
                <div className="flex-1">
                  <p className="font-semibold">{error}</p>
                  {roleMismatch && (
                    <button
                      type="button"
                      onClick={handleProceedWithActualRole}
                      className="mt-2.5 inline-flex items-center gap-1 text-xs font-bold text-blue-700 bg-white px-3 py-1.5 rounded-lg border border-blue-200 hover:bg-blue-50 shadow-sm"
                    >
                      <span>Switch to {roleMismatch.friendlyActual} & Continue</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </button>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* Success Display */}
          {success && (
            <div className="mb-5 p-4 bg-emerald-50 border border-emerald-200 rounded-2xl text-emerald-800 text-sm flex items-center gap-2.5">
              <CheckCircle2 className="w-5 h-5 text-emerald-500 flex-shrink-0" />
              <span className="font-medium">{success}</span>
            </div>
          )}

          {/* Login Form */}
          <form className="space-y-4" onSubmit={handleLogin}>
            {/* Email Field */}
            <div>
              <label htmlFor="email" className="block text-xs font-semibold text-gray-700 mb-1">
                Email Address
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                  <Mail className="h-4 w-4" />
                </div>
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                  className="block w-full pl-10 pr-3 py-2.5 text-sm border border-gray-300 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                  placeholder={
                    selectedRole === 'jobseeker'
                      ? 'candidate@example.com'
                      : 'recruiter@company.com'
                  }
                  disabled={loading}
                />
              </div>
            </div>

            {/* Password Field */}
            <div>
              <div className="flex items-center justify-between mb-1">
                <label htmlFor="password" className="block text-xs font-semibold text-gray-700">
                  Password
                </label>
                <Link
                  href="/forgot-password"
                  className="text-xs font-medium text-blue-600 hover:text-blue-500"
                >
                  Forgot password?
                </Link>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-gray-400">
                  <Lock className="h-4 w-4" />
                </div>
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  required
                  value={formData.password}
                  onChange={handleInputChange}
                  className="block w-full pl-10 pr-10 py-2.5 text-sm border border-gray-300 rounded-xl placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-colors"
                  placeholder="Enter your password"
                  disabled={loading}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3.5 flex items-center text-gray-400 hover:text-gray-600"
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>

            {/* Submit Button */}
            <div className="pt-2">
              <button
                type="submit"
                disabled={loading}
                className={`w-full flex items-center justify-center gap-2 py-3 px-4 border border-transparent rounded-xl shadow-md text-sm font-bold text-white transition-all duration-200 active:scale-98 disabled:opacity-50 disabled:cursor-not-allowed ${
                  selectedRole === 'jobseeker'
                    ? 'bg-blue-600 hover:bg-blue-700 shadow-blue-500/20'
                    : 'bg-indigo-600 hover:bg-indigo-700 shadow-indigo-500/20'
                }`}
              >
                {loading ? (
                  <>
                    <svg
                      className="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        className="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        strokeWidth="4"
                      ></circle>
                      <path
                        className="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                      ></path>
                    </svg>
                    Signing in...
                  </>
                ) : (
                  <>
                    <span>Sign in as {selectedRole === 'jobseeker' ? 'Job Seeker' : 'Recruiter'}</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </div>
          </form>

          {/* Footer: Register link */}
          <div className="mt-6 pt-5 border-t border-gray-100 text-center">
            <p className="text-xs text-gray-500">
              Don't have an account?{' '}
              <Link
                href={`/register?role=${selectedRole}`}
                className="font-bold text-blue-600 hover:text-blue-500 transition-colors"
              >
                Register as {selectedRole === 'jobseeker' ? 'Job Seeker' : 'Recruiter'}
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}