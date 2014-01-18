	<ul>

		  <li>Administração
		   <ul>
			<!--<li><h:commandLink action="#{corrigirHorariosMBean.iniciar}" value="Corrigir Horarios" onclick="setAba('administracao')" /></li> -->		   
			<li><h:commandLink action="#{calendario.iniciar}" value="Calendário Acadêmico" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{horario.listar}" value="Horário de Turmas" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{resultadoAvaliacaoInstitucionalMBean.iniciar}" value="Resultado da Avaliação Institucional" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{motivosTrancamentoMBean.iniciar}" value="Trancamentos da Avaliação Institucional" onclick="setAba('administracao')" /></li>
		   	<li><a href="${ctx}/administracao/recalculo_discentes.jsf?aba=administracao">Recalcular Discentes</a></li>
		   	<li><a href="${ctx}/administracao/recalculo_curriculos.jsf?aba=administracao">Recalcular Estruturas Curriculares</a></li>
		   	<li><a href="${ctx}/administracao/resetar_calculo_discentes.jsf?aba=administracao">Resetar Última Atualização de Totais</a></li>
		   	<li><h:commandLink action="#{historico.buscarDiscenteExcel}" value="Histórico em formato Excel" onclick="setAba('administracao')" /></li>
			<%--<li><h:commandLink action="#{migracaoMarc.executar}" value="Migrar Campos MARC21" onclick="setAba('administracao')" /></li> --%>
			<li><h:commandLink action="#{parametros.iniciar}" value="Parâmetros do Sistema" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.iniciar}" value="Notificação Acadêmica" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.iniciarIndividual}" value="Notificação Individual" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.acompanhar}" value="Acompanhar Notificações Acadêmicas" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{importacaoDiscenteMBean.preImportar}" value="Importar Discente Lato"  onclick="setAba('administracao')"/> </li>
		   <li><h:commandLink action="#{importacaoDiscenteIMDMBean.preImportar}" value="Importar Discentes Convocados do IMD"  onclick="setAba('administracao')"/> </li>
		   </ul>
		  </li>
		  
		  <li>Processamento de Matrícula
		   <ul>
			<li><h:commandLink action="#{preProcessamentoMatricula.iniciar}" value="Pré-Processamento" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{processamentoMatricula.iniciar}" value="Processamento de Matrículas" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{resultadoProcessamentoMatriculaBean.iniciar}" value="Resultado do Processamento de Matrículas" onclick="setAba('administracao')" /></li>
		   	<li><h:commandLink action="#{processamentoFaltas.iniciar}" value="Processamento de Faltas" onclick="setAba('administracao')" /></li>
		   </ul>
		  </li>
		  
		  <li>Processamento de Matrícula IMD
		   <ul>
			<li><h:commandLink id="processamentoMatricula" action="#{matriculaModuloAvancadoMBean.preProcessar}" value="Processamento de Matrícula" onclick="setAba('administracao')"/></li>
		   </ul>
		  </li>
		  
		  
		  <li>Docente Externo
		   <ul>
			<li><h:commandLink action="#{docenteExterno.popular}" value="Cadastrar" onclick="setAba('administracao')" /></li>
			<li><a href="${ctx}/administracao/docente_externo/lista.jsf?aba=administracao">Alterar/Remover</a></li>
		   </ul>
		  </li>
		  
 	</ul>