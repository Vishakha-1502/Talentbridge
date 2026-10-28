'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function RecruiterIndex() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/recruiter/dashboard');
  }, [router]);

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600"></div>
    </div>
  );
}
