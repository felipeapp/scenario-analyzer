	<ul>

		  <li>Administra��o
		   <ul>
			<!--<li><h:commandLink action="#{corrigirHorariosMBean.iniciar}" value="Corrigir Horarios" onclick="setAba('administracao')" /></li> -->		   
			<li><h:commandLink action="#{calendario.iniciar}" value="Calend�rio Acad�mico" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{horario.listar}" value="Hor�rio de Turmas" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{resultadoAvaliacaoInstitucionalMBean.iniciar}" value="Resultado da Avalia��o Institucional" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{motivosTrancamentoMBean.iniciar}" value="Trancamentos da Avalia��o Institucional" onclick="setAba('administracao')" /></li>
		   	<li><a href="${ctx}/administracao/recalculo_discentes.jsf?aba=administracao">Recalcular Discentes</a></li>
		   	<li><a href="${ctx}/administracao/recalculo_curriculos.jsf?aba=administracao">Recalcular Estruturas Curriculares</a></li>
		   	<li><a href="${ctx}/administracao/resetar_calculo_discentes.jsf?aba=administracao">Resetar �ltima Atualiza��o de Totais</a></li>
		   	<li><h:commandLink action="#{historico.buscarDiscenteExcel}" value="Hist�rico em formato Excel" onclick="setAba('administracao')" /></li>
			<%--<li><h:commandLink action="#{migracaoMarc.executar}" value="Migrar Campos MARC21" onclick="setAba('administracao')" /></li> --%>
			<li><h:commandLink action="#{parametros.iniciar}" value="Par�metros do Sistema" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.iniciar}" value="Notifica��o Acad�mica" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.iniciarIndividual}" value="Notifica��o Individual" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{notificacaoAcademica.acompanhar}" value="Acompanhar Notifica��es Acad�micas" onclick="setAba('administracao')" /></li>
		   <li><h:commandLink action="#{importacaoDiscenteMBean.preImportar}" value="Importar Discente Lato"  onclick="setAba('administracao')"/> </li>
		   <li><h:commandLink action="#{importacaoDiscenteIMDMBean.preImportar}" value="Importar Discentes Convocados do IMD"  onclick="setAba('administracao')"/> </li>
		   </ul>
		  </li>
		  
		  <li>Processamento de Matr�cula
		   <ul>
			<li><h:commandLink action="#{preProcessamentoMatricula.iniciar}" value="Pr�-Processamento" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{processamentoMatricula.iniciar}" value="Processamento de Matr�culas" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{resultadoProcessamentoMatriculaBean.iniciar}" value="Resultado do Processamento de Matr�culas" onclick="setAba('administracao')" /></li>
		   	<li><h:commandLink action="#{processamentoFaltas.iniciar}" value="Processamento de Faltas" onclick="setAba('administracao')" /></li>
		   </ul>
		  </li>
		  
		  <li>Processamento de Matr�cula IMD
		   <ul>
			<li><h:commandLink id="processamentoMatricula" action="#{matriculaModuloAvancadoMBean.preProcessar}" value="Processamento de Matr�cula" onclick="setAba('administracao')"/></li>
		   </ul>
		  </li>
		  
		  
		  <li>Docente Externo
		   <ul>
			<li><h:commandLink action="#{docenteExterno.popular}" value="Cadastrar" onclick="setAba('administracao')" /></li>
			<li><a href="${ctx}/administracao/docente_externo/lista.jsf?aba=administracao">Alterar/Remover</a></li>
		   </ul>
		  </li>
		  
 	</ul>