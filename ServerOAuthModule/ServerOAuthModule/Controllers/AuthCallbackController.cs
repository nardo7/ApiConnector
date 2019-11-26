using System.IO;
using System.Linq;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using CodeDomain.DataModel;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;

namespace ServerOAuthModule.Controllers
{
    [Route("api/auth/callback")]
    [ApiController]
    public class AuthCallbackController : ControllerBase
    {

        private readonly ILogger<AuthCallbackController> _logger;

        public AuthCallbackController(ILogger<AuthCallbackController> logger)
        {
            _logger = logger;
        }
        // GET: api/AuthCallback
        [HttpGet()]
        public IEnumerable<string> Get(string code, string state)
        {
            _logger.LogInformation("from get receiving a code");
            if (code == null)
            {
                _logger.LogInformation("code was null");
            }
            else
            {
                using (StreamWriter writer = new StreamWriter("./code"))
                {
                    writer.WriteLine(code);
                    _logger.LogInformation("code stored in txt, done!");
                    writer.Close();
                }
                using (var context = new CodesContext())
                {
                    var result = context.Codes.AsNoTracking().FirstOrDefault(x => x.secret_key == state);
                    if (result == null)
                        _logger.LogInformation("there is not auth for this code request yet, or invalid state");
                    else
                    {
                        result.code = code;
                        context.Entry(result).State = EntityState.Modified;

                        try
                        {
                            context.SaveChanges();
                        }
                        catch (DbUpdateConcurrencyException)
                        {
                            if (!context.Codes.Any(e => e.Id == result.Id))
                            {
                                return new string[] { "NOT FOUND" };
                            }
                        }
                    }

                }
            }
            return new string[] { };
        }

        // GET: api/AuthCallback/5
        [HttpGet("{id}", Name = "GetAuthCallback")]
        public string Get(int id)
        {

            return id.ToString();
        }

        // POST: api/AuthCallback
        [HttpPost]
        public void Post([FromBody] string value)
        {
        }

        // PUT: api/AuthCallback/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE: api/ApiWithActions/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}