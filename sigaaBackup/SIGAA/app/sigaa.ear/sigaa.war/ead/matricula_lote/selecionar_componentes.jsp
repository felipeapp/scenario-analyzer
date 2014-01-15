<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

        <style>
            .scrolls{
                width:500px;
                height:550px;
                overflow:auto;
            }
        </style>

<h2><ufrn:subSistema /> > Escolha de Componentes</h2>

	<div id="ajuda" class="descricaoOperacao">
		1. Digite o codigo do componente e clique em <i>Adicionar</i>;
		<br/>
		2. Se existir alguma turma aberta ela será adicionada;
		<br/>
		3. Caso exista turma, será exibido um quadro com informações sobre a disciplina mostrando os polos que possuem turma aberta;
		<br />
		4. Quando terminar de adicionar todas as disciplinas, clique em <i>Confirmar Matricula</i>.	
	</div>

<f:view>

 	<rich:modalPanel id="panel" width="400" height="600" autosized="true">
        <f:facet name="header">
            <h:panelGroup>
                <h:outputText value="Discentes Selecionados"></h:outputText>
            </h:panelGroup>
        </f:facet> 	
		<h:panelGroup layout="block" styleClass="scrolls">
			<rich:dataTable value="#{loteMatriculas.obj.discentes}" var="d" width="100%">
				<f:facet name="header">
					<rich:columnGroup>
						<rich:column>
							<h:outputText value="Nome"/>
						</rich:column>            			
						<rich:column sortBy="#{d.polo.cidade.nome}">
							<h:outputText value="Polo"/>
						</rich:column>            			
					</rich:columnGroup>
				</f:facet>
            	<rich:column style="text-align: left;">
					<h:outputText value="#{d.matricula}"/> - <h:outputText value="#{d.pessoa.nome}"/>
				</rich:column>
				<rich:column sortBy="#{d.polo.cidade.nome}">
					<h:outputText value="#{d.polo.cidade.nome}"/>
				</rich:column>            			
            </rich:dataTable>
		</h:panelGroup>
		<rich:separator></rich:separator>
		<h:commandButton id="btnFechar" value="Fechar">
			<rich:componentControl for="panel" attachTo="btnFechar" operation="hide" event="onclick"/>
		</h:commandButton>
    </rich:modalPanel>
    <br />
    <h:outputLink value="#" id="link">
        Mostrar relação de alunos selecionados
        <rich:componentControl for="panel" attachTo="link" operation="show" event="onclick"/>
    </h:outputLink>
	<br />
	<br />
	<br />

	<h:form id="busca">
		<table class="formulario" id="formulario" width="50%">
			<caption>Adicionar Turma</caption>
			<tbody>
				<tr>
					<th class="required" width="50%">Código do Componente Curricular:</th>
					<td>
						<h:inputText value="#{loteMatriculas.codigoComponente}" size="7" maxlength="7" onkeyup="CAPS(this)" />
					</td>
				</tr>
				
				<tr>
					<th class="required">Ano - Semestre:</th>
					<td><h:inputText value="#{loteMatriculas.anoComponente}" size="4" maxlength="4" id="anoEntrada" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" /> - 
					 <h:inputText value="#{loteMatriculas.periodoComponente}" size="1" maxlength="1" id="anoPeriodo" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Adicionar" actionListener="#{ loteMatriculas.buscarComponentes }"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	
	<br />
	
	<c:if test="${not empty loteMatriculas.infoPolos}">
		<table class="listagem">
		<caption>Turmas por Polo do componente ${loteMatriculas.codigoComponente}</caption>
			<thead>
				<tr>
					<th>Polo</th>
					<th style="text-align: right;">Qtd Discentes</th>
					<th style="text-align: center;">Existe Turma?</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{loteMatriculas.infoPolos}" var="info">
				<tr>
					<td>${ info.polo.cidade.nome }</td>
					<td style="text-align: right;">${ info.qtdAlunos }</td>
					<td style="text-align: center;">
						<h:graphicImage url="/img/check.png" rendered="#{ info.possuiComponente }"/>
						<h:graphicImage url="/img/cross.png" rendered="#{ not info.possuiComponente }"/>
					</td>
				</tr>
				</c:forEach>
				<tr class="linhaImpar">
					<td colspan="3">
						Foram localizados <strong>${fn:length(loteMatriculas.obj.discentes)}</strong> discentes na busca.
					</td>
				</tr>
			</tbody>
		</table>
	</c:if>
	
	<br />
	
	<c:if test="${not empty loteMatriculas.obj.turmas}">
		<table class="listagem">
		<caption>Turmas Adicionadas</caption>
			<thead>
				<tr>
					<th>Disciplina</th>
					<th>Turma</th>
					<th>Polo</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="codigoAnterior" value=""/>
				<c:set var="corLinha" value="linhaPar"/>
				<c:forEach items="#{loteMatriculas.obj.turmas}" var="t">
				
					<c:if test="${codigoAnterior != t.disciplina.codigo}">
						<c:set var="codigoAnterior" value="${t.disciplina.codigo}"/>
						<c:choose>
							<c:when test="${corLinha eq 'linhaPar'}">
								<c:set var="corLinha" value="linhaImpar"/>
							</c:when>
							<c:when test="${corLinha eq 'linhaImpar'}">
								<c:set var="corLinha" value="linhaPar"/>
							</c:when>							
						</c:choose>
					</c:if>
				
					<tr class="${corLinha}">
						<td>${ t.disciplina.codigo }</td>
						<td>${ t.descricao }</td>
						<td>
							<c:choose>
								<c:when test="${empty t.polo.cidade.nome}">Todos Polos</c:when>
								<c:otherwise>${ t.polo.cidade.nome }</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Confirmar Matricula" action="#{ loteMatriculas.confirmarMatricula }"/>
						<h:commandButton value="Cancelar" action="#{ loteMatriculas.cancelar }" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>			
		</table>
	</c:if>	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>