package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.LoginDto;
import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();

		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		return Optional.of(usuarioRepository.save(usuario));
	
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {
		
		if(usuarioRepository.findById(usuario.getId()).isPresent()) {

			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			if ( (buscaUsuario.isPresent()) && ( buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));
			
		}

		return Optional.empty();
	
	}	

	public Optional<LoginDto> autenticarUsuario(Optional<LoginDto> loginDto) {
        
		var credenciais = new UsernamePasswordAuthenticationToken(loginDto.get().getUsuario(), loginDto.get().getSenha());
		
		Authentication authentication = authenticationManager.authenticate(credenciais);
        
		if (authentication.isAuthenticated()) {

			Optional<Usuario> usuario = usuarioRepository.findByUsuario(loginDto.get().getUsuario());

			if (usuario.isPresent()) {

				loginDto.get().setId(usuario.get().getId());
                loginDto.get().setNome(usuario.get().getNome());
                loginDto.get().setFoto(usuario.get().getFoto());
                loginDto.get().setToken(gerarToken(loginDto.get().getUsuario()));
                loginDto.get().setSenha("");
								
				return loginDto;
			
			}

        } 
            
		return Optional.empty();

    }

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(senha);

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

}