    <ul>
		<li><h:commandLink action="#{ coordMonitoria.situacaoProjeto }" value="Consultar Projetos"  /> </li>
		<li><a href="${ctx}/monitoria/DiscenteMonitoria/meus_projetos.jsf?aba=monitoria">Meus Projetos de Monitoria</a></li>
		<li><h:commandLink action="#{ relatorioMonitor.listar }" value="Meus Relat�rios"  /> </li>
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
		<li> Atividades do M�s / Freq��ncia
			<ul>
				<li><a href="${ctx}/monitoria/DiscenteMonitoria/meus_projetos.jsf?aba=monitoria">Cadastrar</a></li>
				<li><h:commandLink action="#{ atividadeMonitor.listarAtividades }" value="Consultar"  /> </li>
			</ul>
		</li>
	</ul>
	<ul>
		<li><h:commandLink action="#{ agregadorBolsas.iniciarBuscar }" value="Inscrever-se em Sele��o de Monitoria"  /> </li>
		<li><h:commandLink action="#{ discenteMonitoria.popularVisualizarResultados }" value="Visualizar Resultado da Sele��o"  /> </li>
    </ul>