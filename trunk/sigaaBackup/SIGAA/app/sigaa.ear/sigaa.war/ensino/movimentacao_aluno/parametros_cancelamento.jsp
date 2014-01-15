<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2><ufrn:subSistema /> &gt; Cancelamento de Alunos em Lote</h2>
<f:view>
<a4j:keepAlive beanName="jubilamentoMBean"></a4j:keepAlive>
<style>
	.colLeft{text-align: left; width: 95%; }
	.colRight{text-align: right; width: 5%; }	
</style>
<h:form id="form">
<h:outputText value="#{jubilamentoMBean.create}"/>

<div class="descricaoOperacao">
	<p>
		Este caso de uso permite ao gestor de ensino Cancelar o Programa de Discentes. 
		O Cancelamento de Programa é a desvinculação de aluno regular do curso de ${jubilamentoMBean.nivelEnsino == 'G' ? 'graduação' : 'pós-graduação stricto sensu'} 
		sem que tenha integralizado as exigências mínimas para sua conclusão. 
		O cancelamento de programa acarreta o cancelamento da matrícula em todos os componentes curriculares nos quais o aluno esteja matriculado.
		Os cancelamentos podem ocorrer devido a(s) seguinte(s) situação(ões):

		<p>
			<b>ABANDONO (NENHUMA MATRÍCULA)</b> - Caracteriza-se abandono de curso por nenhuma matrícula do aluno quando, em um período letivo regular no qual o programa não esteja trancado, o aluno não realizar sua matrícula online no período estabelecido no calendário acadêmico. 
		</p>
		<p>
 			<b>ABANDONO (NENHUMA INTEGRALIZAÇÃO)</b> - Caracteriza-se abandono de curso por nenhuma integralização do aluno quando, em um período letivo regular, trancar sua matrícula ou reprovar em todos os componentes curriculares no qual esteja matriculado.
		</p>

		<c:if test="${jubilamentoMBean.nivelEnsino == 'G'}">
			<p>
				<b>Decurso de prazo máximo para conclusão do curso</b> - Já o cancelamento por prazo máximo se aplica aos alunos que não concluíram o curso no prazo máximo estabelecido pelo projeto político-pedagógico do curso. Serão compreendidos os discentes com status de <i>ATIVO</i> ou <i>FORMANDO</i> cujo prazo máximo seja igual ou inferior ao ano e período informados para a construção da listagem.
			</p>
			<p>
				<b>Não confirmação de vínculo de ingressantes</b> - O cancelamento por não confirmação de vínculo se aplica aos alunos ingressantes que não tiveram seu vínculo confirmado. Serão compreendidos os discentes com status de <i>ATIVO</i> cujo o ano e período de ingresso sejam informados para construção da listagem.
			</p>
		</c:if>

		<br/>
		<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	    <p>
	        Informe um ou mais anos-períodos em que um mesmo aluno não realizou matrícula ou possui reprovação em todos os componentes curriculares, identificando os alunos passíveis de jubilamento de acordo com o tipo escolhido. 
	        Caso deseje que sejam listados os alunos matriculados no período mas que não obtiveram aproveitamento satisfatório em todos os componentes, ou seja, trancaram e/ou reprovaram todas as disciplinas no período, 
	        selecione a opção <b>Listar Alunos com matrícula(s) no(s) período(s) informado(s)</b>.
        </p>	
        </c:if>
        
        <c:if test="${jubilamentoMBean.jubilamentoPrazoMaximo}">
        <p>
	        A busca usa o campo "Ano-Período de Referência" para localizar os alunos para cancelamento.
	        <br/>
	        Em "Ano-Período de Saída" deve ser informado o valor que de saída do aluno. É este valor que vai ser exibido no Histórico do aluno. 
        </p>
        </c:if>
	</p>
</div>

<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	<center>
		<div class="infoAltRem" style="width:60%">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar Período
			<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Período
		</div>
	</center>
</c:if>

<table class="formulario" width="60%">
<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	<caption>Definição dos parâmetros para cancelamento</caption>
	<tr>
		<th class="required" style="vertical-align: middle">Tipo de Cancelamento:</th>
		<td>
			<h:selectOneMenu id="tipoJubilamento" value="#{jubilamentoMBean.tipoJubilamento}"  valueChangeListener="#{jubilamentoMBean.trocarTipoJubilamento}" onchange="submit();">
				<f:selectItem itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{jubilamentoMBean.allTiposJulilamento}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<c:if test="${jubilamentoMBean.nivelEnsino == 'G'}">
		<tr>
			<th>Modalidade de Ensino:</th>
			<td>
				<h:selectOneRadio value="#{ jubilamentoMBean.ead }" id="ead">
					<f:selectItem itemLabel="Presencial" itemValue="false"/>
					<f:selectItem itemLabel="EAD" itemValue="true"/>
				</h:selectOneRadio>
			</td> 
		</tr>
	</c:if>
	<c:if test="${jubilamentoMBean.nivelEnsino == 'S'}">
		<tr>
			<th>
				Programa:
			</th>				
			<td>
				<h:selectOneMenu id="programa" value="#{jubilamentoMBean.unidade.id}">
					<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
					<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>
	
	<tr>
		<th class="required">
			Ano-Período de Saída:
		</th>				
		<td>
			<h:inputText value="#{ jubilamentoMBean.anoSaida }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoSaida"/>. 
			<h:inputText value="#{ jubilamentoMBean.periodoSaida }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoSaida"/>
			<ufrn:help>
				<p>O valor desse campo será usado como o Ano Período de saída do discente. Como o cancelamento pode ser executado durante um período de transiçao entre semestres, é importante que seja possível definir exatamente qual o período que será feita a operação.</p>
			</ufrn:help>
		</td>
	</tr>
	<c:if test="${jubilamentoMBean.nivelEnsino != 'G'}">
		<tr>
			<th style="text-align: center" colspan="2">
				<h:selectBooleanCheckbox value="#{jubilamentoMBean.filtroMatriculados}" styleClass="noborder" id="checkMatriculados" />
				<h:outputLabel for="checkMatriculados"><b>Listar também alunos com matrícula(s) sem integralização no(s) período(s) informado(s)</b></h:outputLabel>
				
			</th>
		</tr> 	
	</c:if>
	<tr>
		<td colspan="2" class="subFormulario" style="text-align: center;">
			Períodos de Referência:<span class="required"></span>
			<h:inputText value="#{ jubilamentoMBean.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano"/>. 
			<h:inputText value="#{ jubilamentoMBean.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo"/>
			&nbsp;&nbsp;<h:commandButton value="Adicionar" image="/img/adicionar.gif" action="#{ jubilamentoMBean.adicionarAnoPeriodo }" id="add" title="Adicionar Ano-Período"/>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2">
		
		<t:dataTable var="ap" value="#{ jubilamentoMBean.anosPeriodo }"
			rowClasses="linhaPar, linhaImpar" columnClasses="colLeft, colRight" id="datatableDocentes" rowIndexVar="indice">

			<h:column>
				<h:outputText value="#{ ap[0] }"/>.<h:outputText value="#{ ap[1] }"/>
			</h:column>
			
			<h:column headerClass="colRight">
				<h:commandLink id="link" actionListener="#{ jubilamentoMBean.removerAnoPeriodo }">
					<f:param value="#{ indice }" name="indice"/>
					<h:graphicImage url="/img/delete.gif" title="Remover Período" />
				</h:commandLink>
			</h:column>

		</t:dataTable>
		
		</td>
	</tr>

</c:if>

<c:if test="${jubilamentoMBean.jubilamentoPrazoMaximo || jubilamentoMBean.jubilamentoNaoConfirmacaoVinculo}">
	<caption>Prazo Máximo</caption>
	
	
	<tr>
		<th class="required" style="vertical-align: middle">Tipo de Cancelamento:</th>
		<td>
			<h:selectOneMenu id="tipoJubilamento" value="#{jubilamentoMBean.tipoJubilamento}"  valueChangeListener="#{jubilamentoMBean.trocarTipoJubilamento}" onchange="submit();">
				<f:selectItem itemLabel="-- SELECIONE --" />
				<f:selectItems value="#{jubilamentoMBean.allTiposJulilamento}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
		<th class="required">
			Ano-Período de Saída:
		</th>				
		<td>
			<h:inputText value="#{ jubilamentoMBean.anoSaida }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoSaida"/> -  
			<h:inputText value="#{ jubilamentoMBean.periodoSaida }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoSaida"/>
			<ufrn:help>
				<p>O valor desse campo será usado como o Ano Período de saída do discente. Como o cancelamento pode ser executado durante um período de transiçao entre semestres, é importante que seja possível definir exatamente qual o período que será feita a operação.</p>
			</ufrn:help>
		</td>
	</tr>	
	<tr>
		<th align="right" class="required">Ano-Período de Referência:</th>
		<td>
			<h:inputText value="#{ jubilamentoMBean.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" /> - 
			<h:inputText value="#{ jubilamentoMBean.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" />
		</td>
	</tr>
	
</c:if>
<tfoot>
	<tr>
	<td colspan="2">
		<h:commandButton value="Listar Alunos" action="#{ jubilamentoMBean.listar }" id="btnListar"/>
		<h:commandButton value="Cancelar" action="#{ jubilamentoMBean.cancelar }" onclick="#{confirm}" id="btnCancelar"/>
	</td>
	</tr>
</tfoot>
</table>
<br/>
<div class="obrigatorio" style="width:100%">Campo de preenchimento obrigatório.</div>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>