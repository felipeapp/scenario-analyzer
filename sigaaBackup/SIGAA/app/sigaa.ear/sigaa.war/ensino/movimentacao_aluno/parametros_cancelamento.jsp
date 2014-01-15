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
		O Cancelamento de Programa � a desvincula��o de aluno regular do curso de ${jubilamentoMBean.nivelEnsino == 'G' ? 'gradua��o' : 'p�s-gradua��o stricto sensu'} 
		sem que tenha integralizado as exig�ncias m�nimas para sua conclus�o. 
		O cancelamento de programa acarreta o cancelamento da matr�cula em todos os componentes curriculares nos quais o aluno esteja matriculado.
		Os cancelamentos podem ocorrer devido a(s) seguinte(s) situa��o(�es):

		<p>
			<b>ABANDONO (NENHUMA MATR�CULA)</b> - Caracteriza-se abandono de curso por nenhuma matr�cula do aluno quando, em um per�odo letivo regular no qual o programa n�o esteja trancado, o aluno n�o realizar sua matr�cula online no per�odo estabelecido no calend�rio acad�mico. 
		</p>
		<p>
 			<b>ABANDONO (NENHUMA INTEGRALIZA��O)</b> - Caracteriza-se abandono de curso por nenhuma integraliza��o do aluno quando, em um per�odo letivo regular, trancar sua matr�cula ou reprovar em todos os componentes curriculares no qual esteja matriculado.
		</p>

		<c:if test="${jubilamentoMBean.nivelEnsino == 'G'}">
			<p>
				<b>Decurso de prazo m�ximo para conclus�o do curso</b> - J� o cancelamento por prazo m�ximo se aplica aos alunos que n�o conclu�ram o curso no prazo m�ximo estabelecido pelo projeto pol�tico-pedag�gico do curso. Ser�o compreendidos os discentes com status de <i>ATIVO</i> ou <i>FORMANDO</i> cujo prazo m�ximo seja igual ou inferior ao ano e per�odo informados para a constru��o da listagem.
			</p>
			<p>
				<b>N�o confirma��o de v�nculo de ingressantes</b> - O cancelamento por n�o confirma��o de v�nculo se aplica aos alunos ingressantes que n�o tiveram seu v�nculo confirmado. Ser�o compreendidos os discentes com status de <i>ATIVO</i> cujo o ano e per�odo de ingresso sejam informados para constru��o da listagem.
			</p>
		</c:if>

		<br/>
		<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	    <p>
	        Informe um ou mais anos-per�odos em que um mesmo aluno n�o realizou matr�cula ou possui reprova��o em todos os componentes curriculares, identificando os alunos pass�veis de jubilamento de acordo com o tipo escolhido. 
	        Caso deseje que sejam listados os alunos matriculados no per�odo mas que n�o obtiveram aproveitamento satisfat�rio em todos os componentes, ou seja, trancaram e/ou reprovaram todas as disciplinas no per�odo, 
	        selecione a op��o <b>Listar Alunos com matr�cula(s) no(s) per�odo(s) informado(s)</b>.
        </p>	
        </c:if>
        
        <c:if test="${jubilamentoMBean.jubilamentoPrazoMaximo}">
        <p>
	        A busca usa o campo "Ano-Per�odo de Refer�ncia" para localizar os alunos para cancelamento.
	        <br/>
	        Em "Ano-Per�odo de Sa�da" deve ser informado o valor que de sa�da do aluno. � este valor que vai ser exibido no Hist�rico do aluno. 
        </p>
        </c:if>
	</p>
</div>

<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	<center>
		<div class="infoAltRem" style="width:60%">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: Adicionar Per�odo
			<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Per�odo
		</div>
	</center>
</c:if>

<table class="formulario" width="60%">
<c:if test="${jubilamentoMBean.jubilamentoAbandono}">
	<caption>Defini��o dos par�metros para cancelamento</caption>
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
			Ano-Per�odo de Sa�da:
		</th>				
		<td>
			<h:inputText value="#{ jubilamentoMBean.anoSaida }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoSaida"/>. 
			<h:inputText value="#{ jubilamentoMBean.periodoSaida }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoSaida"/>
			<ufrn:help>
				<p>O valor desse campo ser� usado como o Ano Per�odo de sa�da do discente. Como o cancelamento pode ser executado durante um per�odo de transi�ao entre semestres, � importante que seja poss�vel definir exatamente qual o per�odo que ser� feita a opera��o.</p>
			</ufrn:help>
		</td>
	</tr>
	<c:if test="${jubilamentoMBean.nivelEnsino != 'G'}">
		<tr>
			<th style="text-align: center" colspan="2">
				<h:selectBooleanCheckbox value="#{jubilamentoMBean.filtroMatriculados}" styleClass="noborder" id="checkMatriculados" />
				<h:outputLabel for="checkMatriculados"><b>Listar tamb�m alunos com matr�cula(s) sem integraliza��o no(s) per�odo(s) informado(s)</b></h:outputLabel>
				
			</th>
		</tr> 	
	</c:if>
	<tr>
		<td colspan="2" class="subFormulario" style="text-align: center;">
			Per�odos de Refer�ncia:<span class="required"></span>
			<h:inputText value="#{ jubilamentoMBean.ano }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="ano"/>. 
			<h:inputText value="#{ jubilamentoMBean.periodo }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodo"/>
			&nbsp;&nbsp;<h:commandButton value="Adicionar" image="/img/adicionar.gif" action="#{ jubilamentoMBean.adicionarAnoPeriodo }" id="add" title="Adicionar Ano-Per�odo"/>
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
					<h:graphicImage url="/img/delete.gif" title="Remover Per�odo" />
				</h:commandLink>
			</h:column>

		</t:dataTable>
		
		</td>
	</tr>

</c:if>

<c:if test="${jubilamentoMBean.jubilamentoPrazoMaximo || jubilamentoMBean.jubilamentoNaoConfirmacaoVinculo}">
	<caption>Prazo M�ximo</caption>
	
	
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
			Ano-Per�odo de Sa�da:
		</th>				
		<td>
			<h:inputText value="#{ jubilamentoMBean.anoSaida }" size="4" onkeypress="return ApenasNumeros(event);" maxlength="4" id="anoSaida"/> -  
			<h:inputText value="#{ jubilamentoMBean.periodoSaida }" size="1" onkeypress="return ApenasNumeros(event);" maxlength="1" id="periodoSaida"/>
			<ufrn:help>
				<p>O valor desse campo ser� usado como o Ano Per�odo de sa�da do discente. Como o cancelamento pode ser executado durante um per�odo de transi�ao entre semestres, � importante que seja poss�vel definir exatamente qual o per�odo que ser� feita a opera��o.</p>
			</ufrn:help>
		</td>
	</tr>	
	<tr>
		<th align="right" class="required">Ano-Per�odo de Refer�ncia:</th>
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
<div class="obrigatorio" style="width:100%">Campo de preenchimento obrigat�rio.</div>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>