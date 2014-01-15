<ul>
	<li>Vestibular Interno
		<ul>
			<li>
				<h:commandLink value="Importação do Resultado do Vestibular" action="#{processoImportacaDadosProcessoSeletivo.importacaoCandidatosVestibular}" onclick="setAba('importacao')" id="processoImportacaDadosProcessoSeletivo_importacaoCandidatosVestibular"/>
			</li>
		</ul>
	</li>
	<li>Vestibulares/Concursos Externos
		<ul>
			<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.iniciarDefinicaoLeiaute}" value="Definir Leiaute do Arquivo de Importação" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_iniciarDefinicaoLeiaute"/></li>
			<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.listarLeiautes}" value="Listar Leiautes do Arquivo de Importação" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_listarLeiautes"/></li>
			<li><h:commandLink action="#{importaAprovadosOutrosVestibularesMBean.iniciarImportacao}" value="Importar Aprovados" onclick="setAba('importacao')" id="importaInscricaoVestibularMBean_iniciarImportacao"/></li>
		</ul>
	</li>
	<li>Convocação
		<ul>
			<li>
				<h:commandLink value="Convocação de Candidatos para Preenchimento de Vagas" action="#{convocacaoVagasRemanescentesVestibularMBean.iniciar}" onclick="setAba('importacao')" id="convocacaoVagasRemanescentesVestibularMBean_iniciar"/>
			</li>
		</ul>
	</li>
</ul>
