function S_KLC_D(src)
{
	var _p, _a = arguments, _s, _sl, _f, _r, _c, _b, _v, DAYSEC = 86400000, CNSTOCK_BASEDAY = 7657, B64CHARS = [],
	EXPTABLE = [], CUTMASK = ~(3 << 30), CUTBIT = 1 << 30, X3TRANS = [
		0, 3, 5, 6, 9, 10, 12, 15, 17, 18, 20, 23, 24, 27, 29, 30], _M = Math,
	_init = function()
	{
		var i, v;
		for (i = 0; i < 64; i++)
		{
			EXPTABLE[i] = _M.pow(2, i);
			if (i < 26)
			{
				B64CHARS[i] = _asc(i + 65);
				B64CHARS[i + 26] = _asc(i + 97);
				if (i < 10)
				{
					B64CHARS[i + 52] = _asc(i + 48);
				}
			}
		}
		B64CHARS.push('+', '/');
		B64CHARS = B64CHARS.join('');
		_s = src.split('');
		_sl = _s.length;
		for (i = 0; i < _sl; i++)
		{
			_s[i] = B64CHARS.indexOf(_s[i]);
		}
		_f = {};
		_p = _b = 0;
		_c = {};
		v = _decode([12, 6]);
		_v = 63 ^ v[1];
		return {
			_1479: _kl_run, 
			_136: _ml_run, 
			_200: _cl_run, 
			_139: _td_run, 
			_197: _mi_run, 
			_3466: _k2_run
		}['_' + v[0]] || function(){return []};
	},
	_asc = String.fromCharCode,
	_isundef = function(x)
	{
		return x === {}._;
	},
	_findbitldef = function()
	{
		var s, d;
		s = _getbit(
			1 || no_print
			);
		d = 1;
		for(;;)
		{
			if (_getbit(
				1 || no_print
				))
			{
				d++;
			}
			else
			{
				return d * (s * 2 - 1);
			}
		}
	},
	_getbit = function(
		no_print
		)
	{
		var d;
		if (_p >= _sl)
		{
			return 0;
		}
		d = _s[_p] & (1 << _b);
		_b++;
		if (_b >= 6)
		{
			_b -= 6;
			_p++;
		}
		return !!d;
	},
	_decode = function(l, s, a)
	{
		var i, r, d, n, b;
		r = [];
		d = 0;
		if (!s)
		{
			s = [];
		}
		if (!a)
		{
			a = [];
		}
		for (i = 0; i < l.length; i++)
		{
			n = l[i];
			d = 0;
			if (!n)
			{
				r[i] = 0;
				continue;
			}
			if (_p >= _sl)
			{
				return r;
			}
			if (l[i] <= 0)
			{
				d = 0;
			}
			else if (l[i] <= 30)
			{
				for(;;)
				{
					b = 6 - _b;
					b = b < n ? b : n;
					d |= (((_s[_p] >> _b) & ((1 << b) - 1)) << (l[i] - n));
					_b += b;
					if (_b >= 6)
					{
						_b -= 6;
						_p++;
					}
					n -= b;
					if (n <= 0)
					{
						break;
					}
				}
				if (s[i] && d >= EXPTABLE[l[i] - 1])
				{
					d -= EXPTABLE[l[i]];
				}
			}
			else
			{
				d = _decode([30, l[i] - 30], [0, s[i]]);
				if (!a[i])
				{
					d = d[0] + d[1] * EXPTABLE[30];
				}
			}
			r[i] = d;
		}
		return r;
	},
	_decodeday = function()
	{
		var d;
		d = _decode([3])[0];
		if (d == 1)
		{
			_f.d = _decode([18], [1])[0];
			d = 0;
		}
		else if (!d)
		{
			d = _decode([6])[0];
		}
		return d;
	},
	_nextday_old = function(n)
	{
		var i, d, w;
		if (n > 1)
		{
			i = 0;
		}
		for (i = 0; i < n; i++)
		{
			_f.d++;
			w = _f.d % 7;
			if (w == 3 || w == 4)
			{
				_f.d += 5 - w;
			}
		}
		d = new Date();
		d.setTime((CNSTOCK_BASEDAY + _f.d) * DAYSEC);
		return d;
	},
	_nextday = function(n)
	{
		var i, d, w;
		w = _f.wd || 62;
		var print_fd = _f.d;
		for (i = 0; i < n; i++)
		{
			do
			{
				_f.d++;
			} while (!(w & (1 << ((_f.d % 7 + 10) % 7))));
		}
		d = new Date();
		d.setTime((CNSTOCK_BASEDAY + _f.d) * DAYSEC);
		return d;
	},
	_precdiv = function(p)
	{
		var t, n, r;
		if (!p)
		{
			return [0, 0];
		}
		else if (p < 0)
		{
			t = _precdiv(- p);
			return [ - t[0], - t[1]];
		}
		t = p % 3;
		n = (p - t) / 3;
		r = [n, n];
		if (t)
		{
			r[t - 1]++;
		}
		return r;
	},
	_precconv = function(v, op, np)
	{
		var dd, d0, d1;
		if (typeof(op) == 'number')
		{
			d0 = _precdiv(op);
		}
		else
		{
			d0 = op;
		}
		d1 = _precdiv(np);
		dd = [d1[0] - d0[0], d1[1] - d0[1]];
		d0 = 1;
		while (dd[0] < dd[1])
		{
			d0 *= 5;
			dd[1]--;
		}
		while (dd[1] < dd[0])
		{
			d0 *= 2;
			dd[0]--;
		}
		if (d0 > 1)
		{
			v *= d0;
		}
		dd = dd[0];
		v = _decnum(v);
		if (dd < 0)
		{
			while(v.length + dd <= 0)
			{
				v = '0' + v;
			}
			dd += v.length;
			d0 = v.substr(0, dd) - 0;
			if (np === undefined)
			{
				return (d0 + '.' + v.substr(dd)) - 0;
			}
			d1 = v.charAt(dd) - 0;
			if (d1 > 5)
			{
				d0++;
			}
			else if (d1 == 5)
			{
				if (v.substr(dd + 1) - 0 > 0)
				{
					d0++;
				}
				else
				{
					d0 += (d0 & 1);
				}
			}
			return d0;
		}
		else
		{
			for (; dd > 0; dd--)
			{
				v += '0';
			}
		}
		return v - 0;
	},
	_decnum = function(v)
	{
       var n, e, r;
       v = v.toString();
       r = [];
       e = v.toLowerCase().indexOf('e');
       if (e > 0)
       {
        for (n = v.substr(e + 1) - 0; n >= 0; n--)
        {
         r.push(Math.floor(n * Math.pow(10, -n) + 0.5) - 0);
        }
        return r.join('');
       }
       else
       {
        return v;
       }
      },
	_cl_run = function()
	{
		var i, j, r, v, a, x;
		if (_v >= 1)
		{
			return [];
		}
		_f.d = _decode([18], [1])[0] - 1;
		v = _decode([3, 3, 30, 6]);
		_f.p = v[0];
		_f.ld = v[1];
		_f.cd = v[2];
		_f.c = v[3];
		_f.m = _M.pow(10, _f.p);
		_f.pc = _f.cd / _f.m;
		r = [];
		for (i = 0; ; i++)
		{
			a = {d: 1};
			if (_getbit())
			{
				v = _decode([3])[0];
				if (v == 0)
				{
					a.d = _decode([6])[0];
				}
				else if (v == 1)
				{
					_f.d = _decode([18])[0];
					a.d = 0;
				}
				else
				{
					a.d = v;
				}
			}
			x = {day: _nextday_old(a.d)};
			if (_getbit())
			{
				_f.ld += _findbitldef();
			}
			v = _decode([_f.ld * 3], [1]);
			_f.cd += v[0];
			x.close = _f.cd / _f.m;
			r.push(x);
			if (_p >= _sl || (_p == _sl - 1 && !((_f.c ^ (i + 1)) & 63)))
			{
				break;
			}
		}
		r[0].prevclose = _f.pc;
		return r;
	},
	_ml_run = function()
	{
		var i, j, v, a, x, t, r, d, l, tv, f;
		if (_v > 2)
		{
			return [];
		}
		r = [];
		l = {v: 'volume', p: 'price', a: 'avg_price'};
		_f.d = _decode([18], [1])[0] - 1;
		d = {day: _nextday_old(1)};
		v = _decode(_v < 1 ? [3, 3, 4, 1, 1, 1, 5] : [4, 4, 4, 1, 1, 1, 3]);
		for (i = 0; i < 7; i++)
		{
			_f[['la', 'lp', 'lv', 'tv', 'rv', 'zv', 'pp'][i]] = v[i];
		}
		_f.m = _M.pow(10, _f.pp);
		if (_v >= 1)
		{
			v = _decode([3, 3]);
			_f.c = v[0];
			v = v[1];
		}
		else
		{
			v = 5;
			_f.c = 2;
		}
		_f.pc = _decode([v * 6])[0];
		d.pc = _f.pc / _f.m;
		_f.cp = _f.pc;
		_f.da = 0;
		_f.sa = _f.sv = 0;
		for (i = 0; ; i++)
		{
			if (_p >= _sl || (_p == _sl - 1 && !((_f.c ^ i) & 7)))
			{
				break;
			}
			x = {};
			a = {};
			tv = (_f.tv ? _getbit() : 1);
			for (j = 0; j < 3; j++ )
			{
				f = ['v', 'p', 'a'][j];
				if (tv ? _getbit() : 0)
				{
					v = _findbitldef();
					_f['l' + f] += v;
				}
				t = (f == 'v' && _f.rv) ? _getbit() : 1;
				v = _decode([_f['l' + f] * 3 + (f == 'v' ? (t * 7) : 0)], [!!j])[0] * (t ? 1 : 100);
				a[f] = v;
				if (f == 'v')
				{
					if (!(x[l[f]] = v) && ((_v > 1 || i < 241) && (_f.zv ? !_getbit() : 1)))
					{
						a['p'] = 0;
						break;
					}
				}
				else if (f == 'a')
				{
					_f.da = (_v < 1 ? 0 : _f.da) + a['a'];
				}
			}
			_f.sv += a['v'];
			x[l['p']] = (_f.cp += a['p']) / _f.m;
			_f.sa += a['v'] * _f.cp;
			x[l['a']] = _isundef(a['a']) ? (i ? r[i - 1][l['a']] : x[l['p']]) :
				(_f.sv ? (((_M.floor((_f.sa * (2000 / _f.m) + _f.sv) / _f.sv) >> 1) + _f.da) / 1000) 
				: (x[l['p']] + _f.da / 1000));
			r.push(x);
		}
		r[0].date = d.day;
		r[0].prevclose = d.pc;
		return r;
	},
	_kl_run = function()
	{
		var r, i, j, a, x, v, t, f;
		if (_v >= 1)
		{
			return [];
		}
		_f.lv = 0;
		_f.ld = 0;
		_f.cd = 0;
		_f.cv = [0, 0];
		_f.p = _decode([6])[0];
		_f.d = _decode([18], [1])[0] - 1;
		_f.m = _M.pow(10, _f.p);
		v = _decode([3, 3]);
		_f.md = v[0];
		_f.mv = v[1];
		r = [];
		for (;;)
		{
			v = _decode([6]);
			if (!v.length)
			{
				break;
			}
			a = {c: v[0]};
			x = {};
			a.d = 1;
			if (a.c & 32)
			{
				for(;;)
				{
					v = _decode([6])[0];
					if ((v | 16) == 63)
					{
						f = (v & 16) ? 'x' : 'u';
						v = _decode([3, 3]);
						a[f + '_d'] = v[0] + _f.md;
						a[f + '_v'] = v[1] + _f.mv;
						break;
					}
					else if (v & 32)
					{
						t = (v & 8) ? 'd' : 'v';
						f = (v & 16) ? 'x' : 'u';
						a[f + '_' + t] = (v & 7) + _f['m' + t];
						break;
					}
					else
					{
						t = v & 15;
						if (t == 0)
						{
							a.d = _decode([6])[0];
						}
						else if(t == 1)
						{
							_f.d = t = _decode([18])[0];
							a.d = 0;
						}
						else
						{
							a.d = t;
						}
						if (!(v & 16))
						{
							break;
						}
					}
				}
			}
			x.day = _nextday_old(a.d);
			for (t in {v: 0, d: 0})
			{
				if (!_isundef(a['x_' + t]))
				{
					_f['l' + t] = a['x_' + t];
				}
				if (_isundef(a['u_' + t]))
				{
					a['u_' + t] = _f['l' + t];
				}
			}
			a.l_l = [a.u_d, a.u_d, a.u_d, a.u_d, a.u_v];
			f = X3TRANS[a.c & 15];
			if (a.u_v & 1)
			{
				f = 31 - f;
			}
			if (a.c & 16)
			{
				a.l_l[4] += 2;
			}
			for (j = 0; j < 5; j++)
			{
				if (f & (1 << (4 - j)))
				{
					a.l_l[j]++;
				}
				a.l_l[j] *= 3;
			}
			a.d_v = _decode(a.l_l, [1, 0, 0, 1, 1], [0, 0, 0, 0, 1]);
			t = _f.cd + a.d_v[0];
			x.open = (t) / _f.m;
			x.high = (t + a.d_v[1]) / _f.m;
			x.low = (t - a.d_v[2]) / _f.m;
			x.close = (t + a.d_v[3]) / _f.m;
			v = a.d_v[4];
			if (typeof(v) == 'number')
			{
				v = [v, v >= 0 ? 0 : -1];
			}
			_f.cd = t + a.d_v[3];
			f = (_f.cv[0] + v[0]);
			_f.cv = [f & CUTMASK, 
				(_f.cv[1] + v[1] + !!(((_f.cv[0] & CUTMASK) + (v[0] & CUTMASK)) & CUTBIT))];
			x.volume = (_f.cv[0] & (CUTBIT - 1)) + _f.cv[1] * CUTBIT;
			r.push(x);
		}
		return r;
	},
	_td_run = function()
	{
		var r, t, f, c;
		if (_v > 1)
		{
			return [];
		}
		_f.l = 0;
		c = -1;
		_f.d = _decode([18])[0] - 1;
		f = _decode([18])[0];
		while (_f.d < f)
		{
			t = _nextday_old(1);
			if (c <= 0)
			{
				if (_getbit())
				{
					_f.l += _findbitldef();
				}
				c = _decode([_f.l * 3], [0])[0] + 1;
				if (!r)
				{
					r = [t];
					c--;
				}
			}
			else
			{
				r.push(t);
			}
			c--;
		}
		return r;
	};
	_mi_run = function()
	{
		var i, j, r, v, a, x;
		if (_v >= 1)
		{
			return [];
		}
		_f.f = _decode([6])[0];
		_f.c = _decode([6])[0];
		r = [];
		_f.dv = [];
		_f.dl = [];
		for (i = 0; i < _f.f; i++)
		{
			_f.dv[i] = 0;
			_f.dl[i] = 0;
		}
		for (i = 0; ; i++)
		{
			if (_p >= _sl || (_p == _sl - 1 && !((_f.c ^ i) & 7)))
			{
				break;
			}
			a = [];
			for (j = 0; j < _f.f; j++)
			{
				if (_getbit())
				{
					_f.dl[j] += _findbitldef();
				}
				_f.dv[j] += _decode([_f.dl[j] * 3], [1])[0];
				a[j] = _f.dv[j];
			}
			r.push(a);
		}
		return r;
	};
	_k2_run = function()
	{
		_f = {
			b_avp: 1, b_ph: 0, b_phx: 0, b_sep: 0,
			p_p: 6, p_v: 0, p_a: 0, p_e: 0, p_t: 0,
			l_o: 3, l_h: 3, l_l:3, l_c: 3, 
			l_v: 5, l_a: 5, l_e: 3, l_t: 0, 
			u_p: 0, u_v: 0, u_a: 0, 
			wd: 62, d: 0
		};
		if (_v > 0)
		{
			return [];
		}
		var r, x, a, b, d, i, c;
		r = [];
		for (;;)
		{
			if (_p >= _sl)
			{
				return undefined;
			}
			a = {d: 1, c: 0};
			if (_getbit())
			{
				if (_getbit())
				{
					if (_getbit())
					{
						a.c++;
						a.a = _f.b_avp;
						if (_getbit())
						{
							_f.b_avp ^= _getbit();
							_f.b_ph ^= _getbit();
							_f.b_phx ^= _getbit();
							a.s = _f.b_sep;
							_f.b_sep ^= _getbit();
							if (_getbit())
							{
								_f.wd = _decode([7])[0];
							}
							if (a.s ^ _f.b_sep)
							{
								if (a.s)
								{
									_f.u_p = _f.u_c;
								}
								else
								{
									_f.u_o = _f.u_h = _f.u_l = _f.u_c = _f.u_p;
								}
							}
						}
						for (i = 0; i < 3 + _f.b_ph * 2; i++)
						{
							if (_getbit())
							{
								d = 'pvaet'.charAt(i);
								b = _f['p_' + d];
								_f['p_' + d] += _findbitldef();
								_f['u_' + d] = _precconv(_f['u_' + d], b, _f['p_' + d]) - 0;
								if (_f.b_sep && !i)
								{
									for (c = 0; c < 4; c++)
									{
										d = 'ohlc'.charAt(c);
										_f['u_' + d] = _precconv(_f['u_' + d], b, _f.p_p) - 0;
									}
								}
							}
						}
						if (!_f.b_avp && a.a)
						{
							_f.u_a = _precconv((x && x.amount) || 0, 0, _f.p_a);
						}
					}
					if (_getbit())
					{
						a.c++;
						for (i = 0; i < 7 + _f.b_ph + _f.b_phx; i++)
						{
							if (_getbit())
							{
								if (i == 6)
								{
									a.d = _decodeday();
								}
								else
								{
									_f['l_' + 'ohlcva*et'.charAt(i)] += _findbitldef();
								}
							}
						}
					}
					if (_getbit())
					{
						a.c++;
						d = _f.l_o + (_getbit() && _findbitldef());
						b = _decode([d * 3], [1])[0];
						a.p = _f.b_sep ? (_f.u_c + b) : (_f.u_p += b);
					}
					if (!a.c)
					{
						break;
					}
				}
				else
				{
					if (_getbit())
					{
						if (_getbit())
						{
							if (_getbit())
							{
								a.d = _decodeday();
							}
							else
							{
								_f.l_v += _findbitldef();
							}
						}
						else
						{
							if (_f.b_ph && _getbit())
							{
								_f['l_' + 'et'.charAt(_f.b_phx && _getbit())] += _findbitldef();
							}
							else
							{
								_f.l_a += _findbitldef();
							}
						}
					}
					else
					{
						_f['l_' + 'ohlc'.charAt(_decode([2])[0])] += _findbitldef();
					}
				}
			}
			for (i = 0; i < 6 + _f.b_ph + _f.b_phx; i++)
			{
				c = 'ohlcvaet'.charAt(i);
				b = ((_f.b_sep ? 191 : 185) >> i) & 1;
				a['v_' + c] = _decode([_f['l_' + c] * 3], [b])[0];
			}
			x = {day: _nextday(a.d)};
			if (a.p)
			{
				x.prevclose = _precconv(a.p, _f.p_p);
			}
			if (_f.b_sep)
			{
				x.open = _precconv(_f.u_o += a.v_o, _f.p_p);
				x.high = _precconv(_f.u_h += a.v_h, _f.p_p);
				x.low = _precconv(_f.u_l += a.v_l, _f.p_p);
				x.close = _precconv(_f.u_c += a.v_c, _f.p_p);
			}
			else
			{
				a.o = _f.u_p + a.v_o;
				x.open = _precconv(a.o, _f.p_p);
				x.high = _precconv(a.o + a.v_h, _f.p_p);
				x.low = _precconv(a.o - a.v_l, _f.p_p);
				x.close = _precconv(_f.u_p = a.o + a.v_c, _f.p_p);
			}
			x.volume = _precconv(_f.u_v += a.v_v, _f.p_v);
			if (_f.b_avp)
			{
				b = _precdiv(_f.p_p);
				d = _precdiv(_f.p_v);
				x.amount = _precconv(_precconv(Math.floor(
					(_f.b_sep ? (_f.u_o + _f.u_h + _f.u_l + _f.u_c) / 4 : (a.o + (a.v_h - a.v_l + a.v_c) / 4))
					 * _f.u_v + 0.5),
					[b[0] + d[0], b[1] + d[1]], _f.p_a)
					 + a.v_a, _f.p_a);
			}
			else
			{
				_f.u_a += a.v_a;
				x.amount = _precconv(_f.u_a, _f.p_a);
			}
			if (_f.b_ph)
			{
				x.ph_volume = _precconv(a.v_e, _f.p_e);
				x.ph_amount = _precconv(Math.floor(x.ph_volume * x.close + (_f.b_phx ? _precconv(a.v_t, _f.p_t) : 0) + 0.5), 0);
			}
			r.push(x);
		}
		return r;
	};
	if (_decnum(1e21).length != 22)
	{
		_decnum = function(v)
		{
			var n, e, r;
			v = v.toString();
			r = [];
			e = v.toLowerCase().indexOf('e');
			if (e > 0)
			{
				for (n = v.substr(e + 1) - 0; n >= 0; n--)
				{
					r.push(Math.floor(n * Math.pow(10, -n) + 0.5) - 0);
				}
				return r.join('');
			}
			else
			{
				return v;
			}
		};
	}
	return _init()();
};

