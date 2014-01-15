<ul>
	<li>Questionário Sócio Econômico
		<ul>
			<li><h:commandLink action="#{questionarioBean.gerenciarQuestionarioVestibular}" value="Gerenciar questionários (Listar/Alterar/Cadastrar)" onclick="setAba('cadastros')" /></li>
		</ul>
	</li>

	<li>Processos Seletivos
		<ul>
			<li><h:commandLink action="#{processoSeletivoVestibular.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')" id="processoSeletivoVestibular_preCadastrar"/></li>
			<li><h:commandLink action="#{processoSeletivoVestibular.listar}" value="Listar/Alterar" onclick="setAba('cadastros')" id="processoSeletivoVestibular_listar"/></li>
			<li><h:commandLink action="#{avisoProcessoSeletivoVestibular.preCadastrar}" value="Cadastrar Avisos e Notícias" onclick="setAba('cadastros')" id="avisoProcessoSeletivoVestibular_preCadastrar"/></li>
			<li><h:commandLink action="#{avisoProcessoSeletivoVestibular.listar}" value="Listar/Alterar Avisos e Notícias" onclick="setAba('cadastros')" id="avisoProcessoSeletivoVestibular_listar"/></li>
		</ul>
	</li>

	<li>Área de Conhecimento
		<ul>
			<li><h:commandLink action="#{areaConhecimentoVestibular.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')" id="areaConhecimentoVestibular_preCadastrar"/></li>
			<li><a href="${ctx}/vestibular/AreaConhecimentoVestibular/lista.jsf?aba=cadastros">Listar/Alterar</a></li>
		</ul>
	</li>
	
	<li>Região Preferencial de Prova
		<ul>
			<li><a href="${ctx}/vestibular/RegiaoPreferencialProva/form.jsf?aba=cadastros">Cadastrar</a></li>
			<li><a href="${ctx}/vestibular/RegiaoPreferencialProva/lista.jsf?aba=cadastros">Listar/Alterar</a></li>
		</ul>
	</li>
	
	<li>Lingua Estrangeira
		<ul>
			<li><a href="${ctx}/vestibular/LinguaEstrangeira/lista.jsf?aba=cadastros">Listar</a></li>
		</ul>
	</li>
	
	<li>Locais de Aplicação de Prova
		<ul>
			<li><h:commandLink action="#{localAplicacaoProva.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')" id="localAplicacaoProva_preCadastrar"/></li>
			<li><a href="${ctx}/vestibular/LocalAplicacaoProva/lista.jsf?aba=cadastros">Listar/Alterar</a></li>
			<li><h:commandLink action="#{localAplicacaoProcessoSeletivo.iniciar}" value="Associar a um Processo Seletivo" onclick="setAba('cadastros')" id="localAplicacaoProcessoSeletivo_iniciar"/></li>
		</ul>
	</li>
	
	<li>Base de Dados de Escolas do INEP
		<ul>
			<li><h:commandLink action="#{escolaInep.listar}" value="Listar Escolas" onclick="setAba('cadastros')" id="escolaInep_listar"/></li>
		</ul>
	</li>
	
	<li>Isentos da Taxa de Inscrição
		<ul>
			<li><h:commandLink action="#{isencaoTaxaInscricao.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')" id="isencaoTaxaInscricao_preCadastrar"/></li>
			<li><h:commandLink action="#{isencaoTaxaInscricao.listar}" value="Listar/Remover" onclick="setAba('cadastros')" id="isencaoTaxaInscricao_listar"/></li>
		</ul>
	</li>
	
	<li>Restrições à Inscrição do Vestibular
		<ul>
			<li><h:commandLink action="#{restricaoInscricaoVestibular.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')" id="restricaoInscricaoVestibular_preCadastrar"/></li>
			<li><h:commandLink action="#{restricaoInscricaoVestibular.listar}" value="Listar/Alterar" onclick="setAba('cadastros')" id="restricaoInscricaoVestibular_listar"/></li>
		</ul>
	</li>
	<li>Status de Fotos 3x4
		<ul>
			<li><h:commandLink action="#{statusFotoMBean.preCadastrar}" value="Cadastrar Status de Fotos 3x4 dos Candidatos" onclick="setAba('cadastros')" id="statusFotoMBean_preCadastrar"/></li>
			<li><h:commandLink action="#{statusFotoMBean.listar}" value="Listar/Alterar Status de Fotos 3x4 dos Candidatos" onclick="setAba('cadastros')" id="statusFotoMBean_listar"/></li>
		</ul>
	</li>
	<li>Impressão de Relatórios e Documentos
		<ul>
			<li><h:commandLink action="#{documentosDiscentesConvocadosMBean.iniciarImpressaoDocumentos}" value="Impressão para Cadastramento de Discentes" onclick="setAba('cadastros')" id="ImpressaoDocumentos"/></li>
		</ul>
	</li>
	
</ul>