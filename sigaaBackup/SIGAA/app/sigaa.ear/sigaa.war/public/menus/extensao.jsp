<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<dl>
	<dt>
		<div class="opcao AcoesExtensao">
		<h3>A��es de Extens�o</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?aba=p-extensao">
			Consulte as a��es de extens�o desenvolvidas pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProgramaExtensao">
		<h3>Programas</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PROGRAMA%>&aba=p-extensao">
			Consulte os Programas de extens�o desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProjetoExtensao">
		<h3>Projetos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PROJETO%>&aba=p-extensao">
			Consulte os Projetos de extens�o desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao CursoExtensao">
		<h3>Cursos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.CURSO%>&aba=p-extensao">
			Consulte os Cursos de extens�o ministrados pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao EventoExtensao">
		<h3>Eventos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.EVENTO%>&aba=p-extensao">
			Consulte os Eventos de extens�o realizados pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProdutoExtensao">
		<h3>Produtos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PRODUTO%>&aba=p-extensao">
			Consulte os Produtos de extens�o desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao processoGraduacao">
			<h3>Visualizar Cursos ou Eventos</h3>
			<a href="/sigaa/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsf?aba=p-extensao">
				Consulte os Cursos e Eventos de extens�o da ${ configSistema['siglaInstituicao'] } que possuem inscri��es abertas.
			</a>
		</div>
	</dt>
	
	<%-- Redirecionamento definido no arquivo pretty-config.xml --%>
	<dt>
		<div class="opcao processoTransferencia">
			<h3>Acesso � �rea de Inscritos em Cursos e Eventos</h3>
			<a href="/sigaa/link/public/extensao/acessarAreaInscrito">
				Realize o acesso na �rea de inscritos para gerenciar as inscri��es nos cursos e eventos
			</a>
		</div>
	</dt>
	
</dl>