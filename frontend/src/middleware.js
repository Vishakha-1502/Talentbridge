import { NextResponse } from "next/server";

export function middleware(req) {
  const role = req.cookies.get("role")?.value || req.cookies.get("userRole")?.value;
  const url = req.nextUrl.clone();

  // Root redirects
  if (url.pathname === "/jobseeker") {
    url.pathname = "/jobseeker/dashboard";
    return NextResponse.redirect(url);
  }
  if (url.pathname === "/recruiter") {
    url.pathname = "/recruiter/dashboard";
    return NextResponse.redirect(url);
  }

  // Allow these routes for everyone without login
  const publicRoutes = [
    "/landingpage",
    "/login", 
    "/register",
    "/unauthorized",
    "/jobseeker/study-materials",
    "/jobseeker/jobs",
    "/"
  ];

  // Check if current route is public
  const isPublicRoute = publicRoutes.some(route => 
    url.pathname === route || url.pathname.startsWith(route + "/")
  );

  // If no role (not logged in)
  if (!role) {
    if (isPublicRoute) {
      return NextResponse.next();
    }
    // Redirect unauthenticated visitors to login
    url.pathname = "/login";
    return NextResponse.redirect(url);
  }

  // Role-based restrictions for logged in users
  if (url.pathname.startsWith("/recruiter") && role !== "recruiter") {
    url.pathname = "/unauthorized";
    return NextResponse.redirect(url);
  }

  if (url.pathname.startsWith("/jobseeker") && role !== "jobseeker") {
    url.pathname = "/unauthorized";
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

// Apply middleware globally except for static files
export const config = {
  matcher: [
    "/((?!_next/static|_next/image|favicon.ico).*)", 
  ],
};
