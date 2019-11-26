using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using CodeDomain.DataModel;
using CodeDomainClasses;
using System.Security.Cryptography;
using System.Text;
using Microsoft.Extensions.Logging;

namespace ServerOAuthModule.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly CodesContext _context;
        private readonly HashAlgorithm hashAlgorithm;
        private readonly ILogger<AuthCallbackController> _logger;


        public AuthController(CodesContext context, ILogger<AuthCallbackController> logger)
        {
            _context = context;
            hashAlgorithm = SHA256.Create();
            _logger = logger;

        }

        // GET: api/Auth
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Code>>> GetCodes()
        {
            return await _context.Codes.ToListAsync();
        }

        // GET: api/Auth/5
        [HttpGet("{secret_key}")]
        public async Task<ActionResult<string>> GetCode(string secret_key)
        {

            //var code = new Code() { state = state };

            _logger.LogInformation("from get code");
            var code = await _context.Codes.FirstOrDefaultAsync(x => x.secret_key == secret_key);

            if (code == null)
            {
                return NotFound();
            }
            _logger.LogInformation("returning code");

            return code.code;
        }

        // PUT: api/Auth/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for
        // more details see https://aka.ms/RazorPagesCRUD.
        [HttpPut("{id}")]
        public async Task<IActionResult> PutCode(int id, Code code)
        {
            if (id != code.Id)
            {
                return BadRequest();
            }

            _context.Entry(code).State = EntityState.Modified;

            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CodeExists(id))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return NoContent();
        }

        // POST: api/Auth
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for
        // more details see https://aka.ms/RazorPagesCRUD.
        [HttpPost]
        public async Task<ActionResult<Code>> PostCode([FromBody]Dictionary<string, string> state)
        {
            var code = await _context.Codes.FirstOrDefaultAsync(x => x.state == state["state"]);
            if (code == null)
            {
                code = new Code() { state = state["state"] };
                var key = GetHashString(state["state"]);

                code.secret_key = key;
                _context.Codes.Add(code);
                await _context.SaveChangesAsync();
                _logger.LogInformation(string.Format("this key was created {0} from state {1}", key, state["state"]));

            }
            else
            {
                code.code = null;
                _context.Entry(code).State = EntityState.Modified;
                await _context.SaveChangesAsync();
                _logger.LogInformation(string.Format("the state {0} already existed", state["state"]));

            }
            var response = new Dictionary<string, string>();
            response.Add("code", "success");
            response.Add("message", code.secret_key);
            return CreatedAtAction("GetCode", new { secret_key = code.secret_key }, response);
        }

        public byte[] GetHash(string inputString)
        {

            return hashAlgorithm.ComputeHash(Encoding.UTF8.GetBytes(inputString));
        }
        public string GetHashString(string inputString)
        {
            StringBuilder sb = new StringBuilder();
            foreach (byte b in GetHash(inputString))
                sb.Append(b.ToString("X2"));

            return sb.ToString();
        }

        // DELETE: api/Auth/5
        //[HttpDelete("{id}")]
        //public async Task<ActionResult<Code>> DeleteCode(string id)
        //{
        //    var code = await _context.Codes.FindAsync(id);
        //    if (code == null)
        //    {
        //        return NotFound();
        //    }

        //    _context.Codes.Remove(code);
        //    await _context.SaveChangesAsync();

        //    return code;
        //}

        private bool CodeExists(int id)
        {
            return _context.Codes.Any(e => e.Id == id);
        }
    }

}