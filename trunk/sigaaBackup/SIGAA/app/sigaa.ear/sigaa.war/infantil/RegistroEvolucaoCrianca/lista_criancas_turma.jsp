<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="turmaInfantilMBean" />
<f:view>

	<h:form id="form">
		<h2> <ufrn:subSistema /> &gt; Registro da Evolução da Criança </h2>
		
		<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
			<p><b>Caro docente,</b></p>
			<br/>
			<p>
			Selecione um discente abaixo para registrar sua evolução.		
			</p>
		</div>
		
		<table class="visualizacao" style="width:90%;">
			<caption>Dados da Turma</caption>
			<tr>
				<th>Turma:</th>
				<td>
					<h:outputText escape="false" id="turma" value="#{registroEvolucaoCriancaMBean.turma.descricaoTurmaInfantil}"/>
				</td>
			</tr>
			
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText id="local" value="#{registroEvolucaoCriancaMBean.turma.local}"/>
				</td>
			</tr>
			
			<tr>
				<th>Capacidade:</th>
				<td>
					<h:outputText id="capacidade" value="#{registroEvolucaoCriancaMBean.turma.capacidadeAluno}"/>
					aluno(s)
				</td>
			</tr>
		
		</table>
		<br />
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Discente	
			</div>
		</center>

		<table class="listagem" style="width:90%;">
			<caption> Selecione abaixo o discente (${fn:length(registroEvolucaoCriancaMBean.matriculas)}) </caption>
			<thead>
				<tr>
					<th style="text-align: center;"> Matrícula </th>
					<th> Aluno </th>
					<th> Status </th>
					<th>  </th>
				</tr>
			</thead>

				<c:forEach items="#{registroEvolucaoCriancaMBean.matriculas}" var="m" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
						<td>${m.discente.matricula}</td>
						<td>${m.discente.nome}</td>
						<td>${m.discente.statusString}</td>
						<td align="right" width="2%">
							<h:commandLink action="#{registroEvolucaoCriancaMBean.selecionaDiscenteTurma}"
									title="Selecionar Discente" id="selecionarDiscente">
								<h:graphicImage value="/img/seta.gif"/>
								<f:param name="id" value="#{m.id}" />
							</h:commandLink>
					</td>
					</tr>
				</c:forEach>
					    <tfoot>
			<tr>
				<td colspan="4" align="center">
					<h:commandButton id="btnCancelar2" value="Cancelar" action="#{registroEvolucaoCriancaMBean.cancelar}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
		</table>		
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>