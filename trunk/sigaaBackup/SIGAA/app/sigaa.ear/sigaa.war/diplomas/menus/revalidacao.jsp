<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Solicita��es de Revalida��o
		<ul>
		<li> <a href="${ctx}/graduacao/revalidacao_diploma/form_busca.jsf?aba=revalidacao">Cadastrar/Alterar/Remover Solicita��o de Revalida��o de Diploma</a></li>
		</ul>
	</li>
	<li>Relat�rios
		<ul>
		<li> <h:commandLink value="Solicita��es de Revalida��o de Diplomas" action="#{solRevalidacaoDiploma.formRelatorioRevalidacao}" onclick="setAba('revalidacao')" id="report_solicitacaoRevaliacaoDiploma" /> </li>
		</ul>
	</li>
</ul>