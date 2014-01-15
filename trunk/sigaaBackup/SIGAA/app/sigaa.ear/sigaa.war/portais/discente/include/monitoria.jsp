    <ul>
		<li><h:commandLink action="#{ coordMonitoria.situacaoProjeto }" value="Consultar Projetos"  /> </li>
		<li><a href="${ctx}/monitoria/DiscenteMonitoria/meus_projetos.jsf?aba=monitoria">Meus Projetos de Monitoria</a></li>
		<li><h:commandLink action="#{ relatorioMonitor.listar }" value="Meus Relatórios"  /> </li>
	</ul>
	<ul>
		<li> Meus Certificados
			<ul>
				<li><h:commandLink action="#{ documentosAutenticadosMonitoria.participacoesDiscenteUsuarioLogado }" value="Certificados de Projetos"  /> </li>
				<li><h:commandLink action="#{ resumoSid.listarParticipacoesDiscente }" value="Certificados do SID"  /> </li>
			</ul>
		</li>
	</ul>
	<ul>
		<li> Atividades do Mês / Freqüência
			<ul>
				<li><a href="${ctx}/monitoria/DiscenteMonitoria/meus_projetos.jsf?aba=monitoria">Cadastrar</a></li>
				<li><h:commandLink action="#{ atividadeMonitor.listarAtividades }" value="Consultar"  /> </li>
			</ul>
		</li>
	</ul>
	<ul>
		<li><h:commandLink action="#{ agregadorBolsas.iniciarBuscar }" value="Inscrever-se em Seleção de Monitoria"  /> </li>
		<li><h:commandLink action="#{ discenteMonitoria.popularVisualizarResultados }" value="Visualizar Resultado da Seleção"  /> </li>
    </ul>