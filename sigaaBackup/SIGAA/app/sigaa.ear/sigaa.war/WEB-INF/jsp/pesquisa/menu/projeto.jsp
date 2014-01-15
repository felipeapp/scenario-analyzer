<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>


    <div class="secao projeto">
        <h3>Projetos De Pesquisa</h3>
    </div>
    <ul>
        <li>Projeto de Pesquisa
            <ul>
            <li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=popular">Cadastrar Projeto Externo</html:link></li>
			<li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=popular&interno=true">Submeter Proposta de Projeto Interno</html:link></li>
			<li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=listByCoordenador">Renovar Projeto de Pesquisa</html:link></li>
			<li><html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=list&popular=true">Buscar Projetos de Pesquisa</html:link></li>
			</ul>
		</li>
		<li> Planos de Trabalho
            <ul>
			<li><html:link action="/pesquisa/planoTrabalho/wizard?dispatch=popular&idProjeto=1">Solicitar Cotas</html:link></li>
			<li><html:link action="/pesquisa/planoTrabalho/wizard?dispatch=list&page=0">Listar/Alterar</html:link></li>
            </ul>
		</li>
        <li> Bolsas
            <ul>
			<li><html:link action="/pesquisa/indicarBolsista?dispatch=popular">Indicar/Substituir Bolsista</html:link></li>
            </ul>
		</li>
        <li> Relatório Final
            <ul>
			<li><html:link action="/pesquisa/cadastroRelatorioProjeto?dispatch=edit">Submeter</html:link></li>
			<li><html:link action="/pesquisa/cadastroRelatorioProjeto?dispatch=list">Listar/Alterar</html:link></li>
            </ul>
		</li>
        <li>Avaliações
            <ul>
            <li><html:link action="/verPortalConsultor">Portal do Consultor</html:link></li>
			<li><html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=listaAvaliacao&idConsultor=136195">Avaliar Projeto</html:link></li>
			<li><html:link action="/pesquisa/avaliarPlanoTrabalho?dispatch=list&idConsultor=136195"> Avaliar Plano de Trabalho </html:link></li>
			<li><html:link action="/pesquisa/avaliarRelatorioProjeto?dispatch=list&idConsultor=136195"> Avaliar Relatório do Projeto</html:link></li>
            </ul>
		</li>

    </ul>