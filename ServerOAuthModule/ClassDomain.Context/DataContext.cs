using CodeDomainClasses;
using Microsoft.EntityFrameworkCore;

namespace CodeDomain.DataModel
{
    public class CodesContext : DbContext
    {
        public DbSet<Code> Codes { get; set; }

        public CodesContext() : base()
        {

        }
        public CodesContext(DbContextOptions<CodesContext> options) : base(options)
        {

        }

        protected override void OnConfiguring(DbContextOptionsBuilder builderOptions)
        {
            if (!builderOptions.IsConfigured)
            {
                builderOptions.UseSqlServer(@"Server=(localdb)\mssqllocaldb;Database=Codes;Trusted_Connection=True");
            }
        }
        protected override void OnModelCreating(ModelBuilder builder)
        {
            builder.Entity<Code>().HasIndex(x => x.state);
        }
    }
}
