<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<dl>
	<dt>
		<div class="opcao AcoesExtensao">
		<h3>Ações de Extensão</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?aba=p-extensao">
			Consulte as ações de extensão desenvolvidas pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProgramaExtensao">
		<h3>Programas</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PROGRAMA%>&aba=p-extensao">
			Consulte os Programas de extensão desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProjetoExtensao">
		<h3>Projetos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PROJETO%>&aba=p-extensao">
			Consulte os Projetos de extensão desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao CursoExtensao">
		<h3>Cursos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.CURSO%>&aba=p-extensao">
			Consulte os Cursos de extensão ministrados pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao EventoExtensao">
		<h3>Eventos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.EVENTO%>&aba=p-extensao">
			Consulte os Eventos de extensão realizados pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao ProdutoExtensao">
		<h3>Produtos</h3>
		<a href="/sigaa/public/extensao/consulta_extensao.jsf?acao=<%=TipoAtividadeExtensao.PRODUTO%>&aba=p-extensao">
			Consulte os Produtos de extensão desenvolvidos pela ${ configSistema['siglaInstituicao'] }
		</a>
		</div>
	</dt>
	
	<dt>
		<div class="opcao processoGraduacao">
			<h3>Visualizar Cursos ou Eventos</h3>
			<a href="/sigaa/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsf?aba=p-extensao">
				Consulte os Cursos e Eventos de extensão da ${ configSistema['siglaInstituicao'] } que possuem inscrições abertas.
			</a>
		</div>
	</dt>
	
	<%-- Redirecionamento definido no arquivo pretty-config.xml --%>
	<dt>
		<div class="opcao processoTransferencia">
			<h3>Acesso à Área de Inscritos em Cursos e Eventos</h3>
			<a href="/sigaa/link/public/extensao/acessarAreaInscrito">
				Realize o acesso na área de inscritos para gerenciar as inscrições nos cursos e eventos
			</a>
		</div>
	</dt>
	
</dl>