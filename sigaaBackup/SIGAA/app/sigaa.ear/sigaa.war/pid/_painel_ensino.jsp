<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<rich:panel header="I - ENSINO" styleClass="painelAtividades">
			
	<c:if test="${ empty cargaHorariaPIDMBean.listaDocenteTurma and empty cargaHorariaPIDMBean.obj.chEnsinoEad }">
		<div style="font-size: 12px;">
			N�o foram encontradas turmas nesse semestre. 
			<br><br> 
		</div>
	</c:if>
	<rich:panel header="ENSINO PRESENCIAL" rendered="#{ not empty cargaHorariaPIDMBean.listaDocenteTurma }">
		<rich:dataTable var="itemCHEnsino" value="#{cargaHorariaPIDMBean.listaDocenteTurma}" id="chEnsino" 
			rendered="#{ not empty cargaHorariaPIDMBean.listaDocenteTurma }" width="100%" footerClass="total">
			
			<rich:column styleClass="esquerda">
				<f:facet name="header"><f:verbatim>Ano/Per�odo</f:verbatim></f:facet>
				<h:outputText value="#{itemCHEnsino.turma.ano}"/>.<h:outputText value="#{itemCHEnsino.turma.periodo}"/>
			</rich:column>
	
			<rich:column styleClass="esquerda">
				<f:facet name="header"><f:verbatim><div style="text-align: left;">C�digo</div></f:verbatim></f:facet>
				<h:outputText value="#{itemCHEnsino.turma.disciplina.codigo}"/>
			</rich:column>
			
			<rich:column styleClass="esquerda">
					<f:facet name="header"><f:verbatim><div style="text-align: left;">Turma</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.turma.disciplina.nome}"/>
					 	&nbsp; 
				 		<h:outputText value="-" rendered="#{itemCHEnsino.turma.disciplina.nivel != 'R' && itemCHEnsino.turma.ead == false }" /> &nbsp;
					 	<h:outputText value="T#{itemCHEnsino.turma.codigo}" rendered="#{itemCHEnsino.turma.disciplina.nivel != 'R' && itemCHEnsino.turma.ead == false}" />
			</rich:column>
			
			<rich:column styleClass="esquerda">
					<f:facet name="header"><f:verbatim><div style="text-align: left;">N�vel</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.turma.disciplina.nivelDesc}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">CH do Componente</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.turma.chTotalTurma}h" rendered="#{!itemCHEnsino.chResidenciaSemTurma}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">CH do Docente</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.chDedicadaPeriodo}h" rendered="#{!itemCHEnsino.chResidenciaSemTurma}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">CH Semanal Dedicada*</div></f:verbatim></f:facet>
					
					<h:outputText value="#{itemCHEnsino.chDedicadaPeriodo}h" rendered="#{itemCHEnsino.chResidenciaSemTurma}">
						<f:convertNumber maxFractionDigits="2" groupingUsed="false" />
					</h:outputText>
					<h:outputText value="#{itemCHEnsino.chDedicadaSemana}h" rendered="#{!itemCHEnsino.chResidenciaSemTurma}">
						<f:convertNumber maxFractionDigits="2" groupingUsed="false" />
					</h:outputText>
			</rich:column>
			
	        <f:facet name="footer">
    				<f:verbatim><p align="center">
	             		<h:outputText value="TOTAL DE CARGA HOR�RIA DE ENSINO PRESENCIAL: " />
      			 		<span style="color:#229922;"> 
						<h:outputText value="#{cargaHorariaPIDMBean.chTotalEnsinoPresencial}">
							<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
						</h:outputText> 
						<h:outputText value="h"/>
						</span>
               		</p></f:verbatim>		 
	        </f:facet>
		</rich:dataTable>
		<div>
		<p style="color: #555555;font-size: 0.9em;text-align:left">
			* Carga hor�ria semanal � dilu�da durante todo o semestre.
		</p>
		</div>
	</rich:panel>
	<br/>
	<rich:panel header="ENSINO � DIST�NCIA" rendered="#{ not empty cargaHorariaPIDMBean.obj.chEnsinoEad}">
		<rich:dataTable var="itemCHEnsino" value="#{cargaHorariaPIDMBean.obj.chEnsinoEad}" id="chEnsinoEad" 
			rendered="#{ not empty cargaHorariaPIDMBean.obj.chEnsinoEad }" width="100%" footerClass="total">
			
			<rich:column styleClass="esquerda">
				<f:facet name="header"><f:verbatim>Ano/Per�odo</f:verbatim></f:facet>
				<h:outputText value="#{itemCHEnsino.ano}"/>.<h:outputText value="#{itemCHEnsino.periodo}"/>
			</rich:column>
	
			<rich:column styleClass="esquerda">
				<f:facet name="header"><f:verbatim><div style="text-align: left;">C�digo</div></f:verbatim></f:facet>
				<h:outputText value="#{itemCHEnsino.componenteCurricular.codigo}"/>
			</rich:column>
			
			<rich:column styleClass="esquerda">
					<f:facet name="header"><f:verbatim><div style="text-align: left;">Componente Curricular</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.componenteCurricular.nome}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">Turmas</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.qtdTurmas}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">Discentes</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.qtdDiscentes}"/>
			</rich:column>
			
			<rich:column styleClass="direita">
					<f:facet name="header"><f:verbatim><div style="text-align: right;">CH Semanal Dedicada</div></f:verbatim></f:facet>
					<h:outputText value="#{itemCHEnsino.chDedicada}h" />
			</rich:column>
			
	        <f:facet name="footer">
	   				<f:verbatim><p align="center">
	             		<h:outputText value="TOTAL DE CARGA HOR�RIA DE ENSINO � DIST�NCIA: " />
     			 		<span style="color:#229922;"> 
						<h:outputText value="#{cargaHorariaPIDMBean.chTotalEnsinoDistancia}">
							<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
						</h:outputText> 
						<h:outputText value="h"/>
						</span>
	              		</p></f:verbatim>		 
		     </f:facet>
		</rich:dataTable>
	</rich:panel>
	
	<br/>
		
	<rich:panel header="ORIENTA��ES DE ATIVIDADES E ATENDIMENTOS AOS ALUNOS">
	
	<h:outputText value="Abaixo s�o listadas as atividades cujo o componente curricular possui um valor para a carga hor�ria dedicada pelo docente � atividade. Para os componentes que n�o possuem CH dedicada pelo docente, as orienta��es s�o listados no campo \"ORIENTA��O AOS ALUNOS DE GRADUA��O\" mais abaixo" 
		rendered="#{ not empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacao }" />
	
	<rich:dataTable var="item" value="#{cargaHorariaPIDMBean.listaCHOrientacaoGraduacao}" id="listaOrientacao" 
		width="100%" footerClass="total" rendered="#{ not empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacao }">
		
		<rich:column styleClass="esquerda">
			<f:facet name="header"><f:verbatim><div style="text-align: left;">Atividade</div></f:verbatim></f:facet>
			<h:outputText value="#{item.matriculaComponente.componente.codigoNome}"/>
		</rich:column>
		
		<rich:column styleClass="esquerda">
			<f:facet name="header"><f:verbatim><div style="text-align: left;">Discente</div></f:verbatim></f:facet>
			<h:outputText value="#{item.discente.matriculaNome}"/>
		</rich:column>

		<rich:column styleClass="direita">
				<f:facet name="header"><f:verbatim><div style="text-align: right;">CH Dedicada</div></f:verbatim></f:facet>
				<h:outputText value="#{item.chDedicada}" />
		</rich:column>
		
		<rich:column styleClass="direita">
				<f:facet name="header"><f:verbatim><div style="text-align: right;">CH Semanal Dedicada</div></f:verbatim></f:facet>
				
				<h:outputText value="#{item.chDedicadaSemanal}h" >
					<f:convertNumber maxFractionDigits="2" groupingUsed="false" />
				</h:outputText>
		</rich:column>
		
        <f:facet name="footer">
			<f:verbatim><p align="center">
       		<h:outputText value="TOTAL DE CARGA HOR�RIA DE ORIENTA��ES: " />
	 		<span style="color:#229922;"> 
			<h:outputText value="#{cargaHorariaPIDMBean.chTotalOrientacoes}">
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText> 
			<h:outputText value="h"/>
			</span>
       		</p></f:verbatim>		 
	     </f:facet>
	</rich:dataTable>
	
	<p style="margin: 10px">	Caro(a) professor(a), informe as cargas hor�rias semanais dedicadas a atendimentos e orienta��es de atividades de discentes sob sua orienta��o, de acordo com as categorias a seguir: </p>

	<p>
		<div class="direita">
			<b>ATENDIMENTO AOS ALUNOS:</b> M�nimo 2h, M�ximo 4h.
			
				<a4j:region>
					 <h:inputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chAtendimentoAluno}" maxlength="3" size="3" id="chAtendAluno" 
					 onkeypress="return(formatarMascara(this,event,'#.#'))" title="Carga Hor�ria Atendimento Aluno">
					 	<a4j:support reRender="outputChEnsino, outputChHoraPesquisa, outputChOutrasAtiv, outputTotalEnsinoUsado1, outputTotalEnsinoUsado2, outputTotalEnsinoRestante, outputResumo" actionListener="#{cargaHorariaPIDMBean.calcularTotalCargaHorariaEnsino}" event="onblur"></a4j:support>
					</h:inputText>h
				 </a4j:region>
				 <br/>
 				<span class="texto-ajuda">
					Digite a carga hor�ria semanal dedicada a atender alunos fora do hor�rio de aula.
				</span>
		</div>
		
		<div class="direita">
			<b>ORIENTA��O AOS ALUNOS DE GRADUA��O:</b> ${fn:length(cargaHorariaPIDMBean.listaCHOrientacaoGraduacaoSemAtividades)} 
			
			    <h:outputLink value="#_self" id="linkOrientacaoGraduacao" tabindex="1000" 
			    	disabled="#{empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacaoSemAtividades}">
					orienta��o(�es) cadastradas.
			        <rich:componentControl for="panelGraduacao" attachTo="linkOrientacaoGraduacao" operation="show" event="onclick"
			        rendered="#{not empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacaoSemAtividades}"/>
			    </h:outputLink>

				<a4j:region>  
					<h:inputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chOrientacoesAlunosGraduacao}" maxlength="3" size="3" id="chAtendGraduacao" 
						onkeypress="return(formatarMascara(this,event,'#.#'))"  title="Carga Hor�ria Orienta��o Alunos de Gradua��o">
						<a4j:support reRender="outputChEnsino, outputChHoraPesquisa, outputChOutrasAtiv, outputTotalEnsinoUsado1, outputTotalEnsinoUsado2, outputTotalEnsinoRestante, outputResumo" actionListener="#{cargaHorariaPIDMBean.calcularTotalCargaHorariaEnsino}" event="onblur"></a4j:support>
					</h:inputText>h
				</a4j:region>
				 <br/>
				<span class="texto-ajuda">
					Digite a carga hor�ria semanal dedicada a orienta��es acad�micas aos alunos de gradua��o.
				</span>
		</div>
		
		<div class="direita">
			<b>ORIENTA��O AOS ALUNOS DE P�S-GRADUA��O:</b> ${fn:length(cargaHorariaPIDMBean.listaCHOrientacaoPosGraduacao)} 
			
			    <h:outputLink value="#_self" id="linkOrientacaoPosGraduacao" tabindex="1000">
					orienta��o(�es) cadastradas.
			        <rich:componentControl for="panelPosGraduacao" attachTo="linkOrientacaoPosGraduacao" operation="show" event="onclick" />
			    </h:outputLink>
			
				<a4j:region> 
					<h:inputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chOrientacoesAlunosPosGraduacao}" maxlength="3" size="3" id="chAtendPosGraduacao" title="Carga Hor�ria Orienta��o Alunos de P�s-Gradua��o"
					 	onkeypress="return(formatarMascara(this,event,'#.#'))">
						<a4j:support reRender="outputChEnsino, outputChHoraPesquisa, outputChOutrasAtiv, outputTotalEnsinoUsado1, outputTotalEnsinoUsado2, outputTotalEnsinoRestante, outputResumo" actionListener="#{cargaHorariaPIDMBean.calcularTotalCargaHorariaEnsino}" event="onblur"></a4j:support>
					</h:inputText>h
				</a4j:region>
				 <br/>
				<span class="texto-ajuda">
					Digite a carga hor�ria semanal dedicada a orienta��es acad�micas aos alunos de p�s-gradua��o.
				</span>
		</div>

	</p>
	</rich:panel>

	<a4j:outputPanel id="outputChEnsino" styleClass="quadroTotais">
			<h:outputText value="TOTAL DE CARGA HOR�RIA DE ENSINO:"/>
			<span class="total"> 
			<h:outputText value="#{cargaHorariaPIDMBean.obj.totalGrupoEnsino}">
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText> 
			<h:outputText value="h"/>
			</span>
	</a4j:outputPanel>			
</rich:panel>