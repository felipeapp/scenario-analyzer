<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

<style>
div.painel-modulo {
	position: relative;
}

div.lista-operacoes { width: 230px; background:#E6ECF7; padding: 3px; float: left; margin-right: 10px;}
div.lista-operacoes h3 { background: #C4D2EB; padding: 3px 0 3px 24px; font-variant: small-caps; }

div.painel-modulo ul{ list-style: none; margin: 3px; padding: 2px;}
div.painel-modulo ul ul{ padding: 2px 0; margin: 0;}

div.painel-modulo ul li{ font-weight: bold; color: #003395; padding-top: 2px;}
div.painel-modulo ul ul li{ font-weight: normal; padding: 1px 0;  background: #F9FBFD }
div.painel-modulo ul ul li a{ display: block; padding: 0 2px; padding-left: 8px; color: #404E82}
div.painel-modulo ul ul li a:hover{ background: #FDFAF6; color: #433F0F }

</style>

<div class="painel-modulo">

	<div class="lista-operacoes">
	<h3> Coordenação </h3>
	<ul>
<%-- 
 		<li>Bolsista
            <ul>
                <li> <html:link action="/ensino/latosensu/cadastroBolsistaLato.do?dispatch=edit">Cadastrar</html:link></li>
                <li><html:link action="/ensino/latosensu/cadastroBolsistaLato.do?dispatch=list">Alterar/Remover</html:link></li>
            </ul>
        </li>
 --%>
		<li>Curso
        	<ul>
        		<c:if test="${acesso.coordenadorCursoLato}">
        			<li><h:commandLink value="Identificar Secretário" action="#{secretariaUnidade.iniciarCoordenacaoLato}"/></li>
        			<li><h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCoordenacaoLato}" value="Substituir Secretário"/></li>
        		</c:if>
<%--                <li><html:link action="/ensino/latosensu/criarCurso.do?dispatch=list&renovar=true">Renovar Proposta</html:link></li>
--%>                <li><%-- <html:link action="/ensino/latosensu/cadastroSolicitacaoProrrogacaoPrazo.do?dispatch=edit">--%>Solicitar Prorrogação de Prazo</li>
                <li><h:commandLink value="Submeter Relatório Final" action="#{relatorioLato.iniciar}"/></li>
			</ul>
        <li>Discente
            <ul>
                <li><html:link action="/pessoa/wizard?dispatch=popular&nextView=dadosDiscente">Cadastrar e Matricular</html:link></li>
                <li><html:link action="/ensino/discente/wizard.do?dispatch=list">Buscar/Alterar</html:link></li>
		        <li><%-- <html:link action="/ensino/latosensu/matriculaPreInscrito.do?dispatch=listar">--%>Matricular Discente Pré-inscrito</li>
		        <li>Histórico</li>
            </ul>
        <li>Disciplina
            <ul>
                <li><ufrn:link value="Buscar/Alterar" action="ensino/cadastroDisciplina" param="dispatch=list&page=0"  /></li>
            </ul>
        </li>
        <li>Matrícula
        	<ul>
        		<li><a href="${ ctx }/ensino/tecnico/matricula/tipoMatricula.jsf">Efetuar Matrícula em Turma</a></li>
        		<li><h:commandLink value="Consulta Geral de Turmas" actionListener="#{menuDocente.consultaTurma}"/></li>
        	</ul>
        </li>
        <li>Movimentação de Aluno
            <ul>
            	<li><h:commandLink action="#{movimentacaoAluno.iniciarAfastamento}" value="Cadastrar Afastamento" /></li>
                <li><h:commandLink action="#{movimentacaoAluno.iniciarConclusaoPrograma }" value="Concluir Programa"/></li>
				<li><h:commandLink action="#{movimentacaoAluno.iniciarRetorno }" value="Destrancar Programa" /></li>
				<li><h:commandLink action="#{movimentacaoAluno.iniciarCancelamentoPrograma}" value="Cancelar Programa" /></li>
				<li><h:commandLink action="#{movimentacaoAluno.iniciarEstorno }" value="Estornar Afastamento" /></li>
            </ul>
		</li>
        <li>Pessoa Jurídica
            <ul>
                <li><html:link action="/pessoa/wizard?dispatch=popular&tipoPessoa=J">Cadastrar</html:link></li>
                <li><html:link action="/pessoa/wizard?dispatch=list&tipoPessoa=J">Alterar/Remover</html:link></li>
				<li><html:link action="/ensino/latosensu/criarParceria?dispatch=edit">Atribuir Parceria a Curso</html:link></li>
				<li><html:link action="/ensino/latosensu/criarParceria?dispatch=list">Alterar/Remover Parceria de Curso</html:link></li>
            </ul>
 		<li>Trabalho Final
			<ul>
		        <li><h:commandLink value="Cadastrar Orientações" action="#{teseOrientada.novaEspecializacao }"/></li>
			</ul>
        </li>
		<li>Turma
			<ul>
				<li><html:link action="/ensino/criarTurma?dispatch=popular">Criar Turma</html:link></li>
				<li><html:link action="/ensino/criarTurma?dispatch=list">Alterar/Remover Turma</html:link></li>
        		<li><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf?gestor=true">Consolidar Turma</a></li>
			</ul>
        </li>
	</ul>
	</div>
</div>

<div style="margin: 0 10px 0 250px" >

	<div style="background: #FDFAF6; min-height: 50px; padding: 10px;">
		<h3> Notícias </h3>
			<ul>
				<c:forEach items="${noticiasLato}" var="noticia">
					<li> <html:link action="/ensino/latosensu/cadastroNoticiaLato?dispatch=verNoticia&id=${noticia.id}" > ${noticia.titulo} </html:link> </li>
				</c:forEach>
			</ul>
	</div>

	<h3>Acompanhamento do Curso</h3>
	<h:outputText value="#{lato.create}" />
	<br />
	<c:set var="curso" value="${usuario.cursoLato}"></c:set>
	<c:set var="acompanhamento" value="${lato.acompanhamento}"></c:set>
	<table class="listagem">
		<thead>
			<tr>
				<td colspan="2"><b>${curso.descricao}</b> </td>
			</tr>
		</thead>
		<tbody>
				<tr class="linhaImpar">
					<td> <b>Disciplinas</b> </td>
					<td align="right"> <b>Situação</b> </td>
				</tr>
				<tr>
					<td colspan="2">
						<table width="100%">
						<c:forEach items="${acompanhamento}" var="item" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
								<td>${item.value.descricaoDisciplina}</td>
								<td align="center">${item.value.situacao}</td>
							</tr>
						</c:forEach>
						</table>
					</td>
				</tr>
		</tbody>
	</table>
</div>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>