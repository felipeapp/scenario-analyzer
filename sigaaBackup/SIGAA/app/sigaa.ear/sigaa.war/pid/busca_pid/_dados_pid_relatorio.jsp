<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<rich:panel header="I - ENSINO" styleClass="painelAtividades">

	<c:if test="${ empty cargaHorariaPIDMBean.listaDocenteTurma and empty cargaHorariaPIDMBean.obj.chEnsinoEad }">
		<div style="font-size: 12px;">
			N�o foram encontradas turmas para o per�odo de refer�ncia 
			<br><br> 
		</div>
	</c:if>
	
	<rich:panel header="ENSINO PRESENCIAL" rendered="#{ not empty cargaHorariaPIDMBean.listaDocenteTurma}">
		<rich:dataTable var="itemCHEnsino" value="#{cargaHorariaPIDMBean.listaDocenteTurma}" id="chEnsinoRelatorio" 
			rendered="#{ not empty cargaHorariaPIDMBean.listaDocenteTurma }" width="100%" footerClass="total">
			
			<rich:column styleClass="esquerda">
				<f:facet name="header"><f:verbatim>Per�odo</f:verbatim></f:facet>
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
					<h:outputText value="#{itemCHEnsino.turma.disciplina.nivelDesc}" />
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
					<f:facet name="header"><f:verbatim><div style="text-align: right;">CH Dedicada</div></f:verbatim></f:facet>
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
</rich:panel>

<rich:panel header="ORIENTA��ES DE ATIVIDADES E ATENDIMENTOS AOS ALUNOS" rendered="#{not empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacao}" styleClass="painelAtividades">

	<rich:dataTable var="item" value="#{cargaHorariaPIDMBean.listaCHOrientacaoGraduacao}" id="listaOrientacao" 
		rendered="#{ not empty cargaHorariaPIDMBean.listaCHOrientacaoGraduacao }" width="100%" footerClass="total">
		
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
</rich:panel>

<rich:panel header="CARGA HOR�RIA DEDICADA A ORIENTA��ES E ATENDIMENTOS A ALUNOS" styleClass="painelAtividades subPainel">
		<table>
			<tr>
				<th> ATENDIMENTO AOS ALUNOS: </th>
				<td> 
					<h:outputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chAtendimentoAluno}">
						<f:convertNumber minFractionDigits="1" />
					</h:outputText> h  
				</td>
			</tr>
			<tr>
				<th> ORIENTA��ES DE ALUNOS DE GRADUA��O: </th>
				<td>
					<h:outputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chOrientacoesAlunosGraduacao}"> 
						<f:convertNumber minFractionDigits="1" />
					</h:outputText> h - ${fn:length(cargaHorariaPIDMBean.listaCHOrientacaoGraduacaoSemAtividades)} orienta��o(�es) encontrada(s)
				</td>
			</tr>
			<tr>
				<th> ORIENTA��ES P�S-GRADUA��O: </th>
				<td> 
					<h:outputText value="#{cargaHorariaPIDMBean.obj.chEnsino.chOrientacoesAlunosPosGraduacao}"> 
						<f:convertNumber minFractionDigits="1" />
					</h:outputText> h - ${fn:length(cargaHorariaPIDMBean.listaCHOrientacaoPosGraduacao)} orienta��o(�es) encontrada(s)
				</td>
			</tr>
		</table>
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


<rich:panel header="II - OUTRAS ATIVIDADES DE ENSINO, PESQUISA, EXTENS�O E ADMINISTRA��O" styleClass="painelAtividades">

	<table class="listagem" style="border-bottom: 1px solid #C0C0C0; border-left: 1px solid #C0C0C0; border-right: 1px solid #C0C0C0; border-top: 1px solid #C0C0C0;">
		<thead>
		<tr>
			<th class="dr-table-subheadercell rich-table-subheadercell"> Atividade </th>
			<th class="dr-table-subheadercell rich-table-subheadercell"> Dados </th>
			<th class="dr-table-subheadercell rich-table-subheadercell"> Dedica��o</th> 
			<th class="dr-table-subheadercell rich-table-subheadercell"> CH Semanal</th>
		</tr>
		</thead>
		
		<tbody>
		<tr>
			<td>OUTRAS ATIVIDADES DE ENSINO</td>
			<td>
				${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoEnsino)} projetos de ensino ativo(s).
			</td>
			<td align="right">					
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.percentualOutrasAtividadesEnsino}" />%
			</td>
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.chSemanalOutrasAtividadesEnsino}" >
					<f:convertNumber minFractionDigits="1"  maxFractionDigits="1"/>
				</h:outputText> h  
			</td>
		</tr>
		
		<tr>
			<td>PESQUISA E PRODU��O ACAD�MICA</td>
			<td nowrap="nowrap">${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoPesquisa)} projetos ativo(s).</td>
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.percentualPesquisa}" />%
			</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.chPesquisa}">
					<f:convertNumber minFractionDigits="1"  maxFractionDigits="1" />
				</h:outputText> h  				
			</td>
		</tr>
		
		<tr>
			<td>EXTENS�O E OUTRAS ATIVIDADES</td>
			<td nowrap="nowrap">${fn:length(cargaHorariaPIDMBean.listaCargaHorariaProjetoExtensao)} a��es ativa(s).</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.percentualExtensao}" />%
			</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chProjeto.chExtensao}" >
					<f:convertNumber minFractionDigits="1" maxFractionDigits="1"/>
				</h:outputText> h  				
			</td>
		</tr>
		
		<tr>
			<td>FUN��ES ADMINISTRATIVAS</td>
			<td> 
				<c:set var="tmp" value="${ fn:length(cargaHorariaPIDMBean.listaChAdmin)}" />
				 
				<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaChAdmin}" varStatus="status">
					<c:if test="${ fn:length(cargaHorariaPIDMBean.listaChAdmin) > 1 }">

						<c:if test="${status.count < tmp}">
							${item.atividade.descricao}, 
						</c:if>
						<c:if test="${status.count == tmp}">
							${item.atividade.descricao} 
						</c:if>
						
					</c:if>
				  	<c:if test="${ fn:length(cargaHorariaPIDMBean.listaChAdmin) == 1 }">
						${item.atividade.descricao}
					</c:if>
				</c:forEach>
			</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.percentualAdministracao}" />%
			</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chTotalAdministracao}" >
					<f:convertNumber minFractionDigits="1" maxFractionDigits="1"/>
				</h:outputText> h  	
			</td>
		</tr>
		
		<tr>
			<td>Outras atividades Desenvolvidas em Cursos de Gradua��o e p�s-gradua��o e/ou outros projetos institucionais com remunera��o espec�fica, mediante autoriza��o do CONSEPE</td>
			<td></td>
			
			<td align="right">					
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.percentualOutrasAtividades}" />%
			</td>
			
			<td align="right">
				<h:outputText value="#{cargaHorariaPIDMBean.obj.chOutrasAtividades.chSemanalOutrasAtividades}" >
					<f:convertNumber minFractionDigits="1" maxFractionDigits="1"/>
				</h:outputText> h  	
			</td>
		</tr>
		
	</table>
</rich:panel>		

<rich:panel header="OUTRAS ATIVIDADES SELECIONADAS" styleClass="painelAtividades subPainel">
		
	<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoEnsino}">
	<div class="opcoesAtividades">
		<div><b>Outras atividades de Ensino</b></div>
		<table class="opcoesAtividades">
			<tr>
				<td></td>
			</tr>
				<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoEnsino}">
					<c:if test="${item.selecionada}"> 
						<tr>
							<td>
								<br> <h:outputText value="#{item.denominacao}"/>  <br>
									
								<div>
									<c:if test="${not empty item.observacao}">
										OBS: <h:outputText value="#{item.observacao}" />
									</c:if>
								</div>
							</td>
						</tr>
					</c:if> 
				</c:forEach>
			</table>
	</div>
	</c:if>
	
	<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec}">
	<div class="opcoesAtividades">
	<div><b>Pesquisa e produ��o t�cnica cient�fica</b></div>
		<table class="opcoesAtividades">
		<tr>
			<td></td>
		</tr>
			<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivPesqProducaTec}">
					<c:if test="${item.selecionada}"> 
						<tr>
							<td>
								<br> <h:outputText value="#{item.denominacao}"/>  <br>
									
								<c:if test="${not empty item.observacao}">
									OBS: <h:outputText value="#{item.observacao}" />
								</c:if>
							</td>
						</tr>
					</c:if>
			</c:forEach>
		</table>
	</div>
	</c:if>

	<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivExtensao}">
	<div class="opcoesAtividades">
	<div><b>Extens�o ou outras atividades t�cnicas</b></div>
		<table>
			<tr>
				<td></td>
			</tr>
				<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivExtensao}">
					<c:if test="${item.selecionada}"> 
						<tr>
							<td>
							 	<br> <h:outputText value="#{item.denominacao}"/>  <br>
									
								<c:if test="${not empty item.observacao}">
									OBS: <h:outputText value="#{item.observacao}" /> 
								</c:if>
							</td>
						</tr>
					</c:if>
				</c:forEach>
		</table>
	</div>
	</c:if>

	<c:if test="${not empty cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
	<div class="opcoesAtividades">
	<div><b>Administra��o</b></div>
		<table class="opcoesAtividades">
		<tr>
			<td></td>
		</tr>
			<c:forEach var="item" items="#{cargaHorariaPIDMBean.listaGrupoAtivAdmin}">
					<c:if test="${item.selecionada}"> 
						<tr>
							<td>
								<br> <h:outputText value="#{item.denominacao}"/>  <br>
									
								<c:if test="${not empty item.observacao}">
									OBS: <h:outputText value="#{item.observacao}" />
								</c:if>
							</td>
						</tr>
					</c:if>
			</c:forEach>
		</table>
	</div>
	</c:if>
</rich:panel>

<rich:panel header="OUTRAS ATIVIDADES ESPECIFICADAS PELO DOCENTE" styleClass="painelAtividades">
	<div>
		<div class="opcoesAtividades">
		<div><b>Descri��o atividade</b></div>
		<table class="opcoesAtividades">
			<tr>
				<td></td>
			</tr>
			<c:forEach var="item" items="#{cargaHorariaPIDMBean.ativEspecificasAdicionasDocente}">
				<tr>
					<td><h:outputText value="#{item.denominacao}" /> </td>
				</tr>
			</c:forEach>
		</table>
		</div>
	</div>
</rich:panel>

	<div class="quadroTotais">
		<h:outputText value="#{cargaHorariaPIDMBean.obj.servidor.regimeTrabalho}" styleClass="total" /> (CH do regime de trabalho)	
		-		
		<a4j:outputPanel id="outputTotalEnsinoUsado1">
			<h:outputText value="#{cargaHorariaPIDMBean.obj.totalGrupoEnsino}" styleClass="total">
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText> (CH dedicada ao ensino)
		</a4j:outputPanel>
			=
		<a4j:outputPanel id="outputTotalEnsinoRestante"> 
			<h:outputText value="#{cargaHorariaPIDMBean.chRestanteADistribuir}" styleClass="total">
				<f:convertNumber maxFractionDigits="1" groupingUsed="false" />
			</h:outputText>
		</a4j:outputPanel>h DEDICADAS A OUTRAS ATIVIDADES
	</div>

<rich:panel header="OBSERVA��ES GERAIS" id="observacao" styleClass="painelAtividades" rendered="#{not empty cargaHorariaPIDMBean.obj.observacao}">
	<div style="text-align: left; margin-left: 20px; text-indent: 2em;">
		<h:outputText id="observacaoGeral" value="#{cargaHorariaPIDMBean.obj.observacao}" />
	</div>
</rich:panel>
