<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Solicitações de Revalidação
		<ul>
		<li> <a href="${ctx}/graduacao/revalidacao_diploma/form_busca.jsf?aba=revalidacao">Cadastrar/Alterar/Remover Solicitação de Revalidação de Diploma</a></li>
		</ul>
	</li>
	<li>Relatórios
		<ul>
		<li> <h:commandLink value="Solicitações de Revalidação de Diplomas" action="#{solRevalidacaoDiploma.formRelatorioRevalidacao}" onclick="setAba('revalidacao')" id="report_solicitacaoRevaliacaoDiploma" /> </li>
		</ul>
	</li>
</ul>