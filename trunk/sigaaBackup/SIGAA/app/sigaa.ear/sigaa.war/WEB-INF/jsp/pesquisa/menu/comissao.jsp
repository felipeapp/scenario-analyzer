<ul>
	<li> Consultas
		<ul>
		<li> <html:link action="/pesquisa/cadastroAreaConhecimento?dispatch=list&page=0">�reas de Conhecimento</html:link></li>
		<li> Projetos de Pesquisa </li>
		<li> Planos de Trabalho</li>
		<li> <html:link action="/pesquisa/resumoCongresso?dispatch=relatorio&popular=true&aba=iniciacao"> Resumos do CIC </html:link> </li>
		<li> <html:link action="/pesquisa/avaliarRelatorioProjeto?dispatch=listRelatoriosFinais&popular=true&aba=relatorios"> Relat�rios Finais de Projeto</html:link></li>
		</ul>
	</li>
	<li> Grupos de Pesquisa
		<ul>
		<li> <h:commandLink action="#{autorizacaoGrupoPesquisaMBean.listarGruposPesquisaPendentes}" value="Autorizar Grupo de Pesquisa"/> </li>
		</ul>
	</li>	
	<li> Relat�rios
		<ul>
		<li> <h:commandLink action="#{producao.verRelatorioCotas}" value="Relat�rio para Concess�o de Cotas de Pesquisa"/> </li>
		</ul>
	</li>	
</ul>
