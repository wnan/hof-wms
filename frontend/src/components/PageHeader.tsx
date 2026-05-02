import { ReactNode } from "react";
import { ChevronRight } from "lucide-react";
import { Link } from "react-router-dom";

interface Crumb { label: string; to?: string; }

interface Props {
  title: string;
  subtitle?: string;
  breadcrumbs?: Crumb[];
  actions?: ReactNode;
}

export function PageHeader({ title, subtitle, breadcrumbs, actions }: Props) {
  return (
    <div className="space-y-2">
      {breadcrumbs && breadcrumbs.length > 0 && (
        <nav className="flex items-center text-xs text-muted-foreground">
          {breadcrumbs.map((c, i) => (
            <span key={i} className="flex items-center">
              {c.to ? <Link to={c.to} className="hover:text-foreground">{c.label}</Link> : <span>{c.label}</span>}
              {i < breadcrumbs.length - 1 && <ChevronRight className="h-3 w-3 mx-1" />}
            </span>
          ))}
        </nav>
      )}
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="page-title">{title}</h1>
          {subtitle && <p className="page-subtitle">{subtitle}</p>}
        </div>
        {actions && <div className="flex items-center gap-2">{actions}</div>}
      </div>
    </div>
  );
}
