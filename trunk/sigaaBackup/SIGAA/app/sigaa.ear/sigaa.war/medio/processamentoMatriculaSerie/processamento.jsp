<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2><ufrn:subSistema /> &gt; ${processamentoMatriculaDiscenteSerie.labelCombo} de Consolida��o de Discentes em S�rie</h2>

<h:form id="form">

	<div class="descricaoOperacao">
		<p>Utilize esta opera��o para ${processamentoMatriculaDiscenteSerie.processamento ? 'realizar a consolida��o' : 'emitir o resultado'} da situa��o dos discentes de determinado curso por s�rie.</p>
		<br/>
		<ul>
			<li>Informe o ano e curso para ${processamentoMatriculaDiscenteSerie.processamento ? 'solicitar o processamento' : 'emitir o resultado'} da situa��o dos discentes na s�rie com base nos aproveitamentos 
			das disciplinas dos mesmos.</li>
			<li>O aluno com uma ou mais reprova��es em disciplinas do ano ser� considerado como <b>REPROVADO</b> na s�rie. 
			Possibilitando o coordenador caracteriz�-lo como <b>APROVADO EM DEPEND�NCIA</b> na opera��o destinada a tal.</li>
		</ul>	 
	</div>

	<table class="formulario" width="60%">
	<caption>Dados do Processamento para Consolida��o de S�rie</caption>
	<tbody>
		<tr>
			<th class="obrigatorio">Ano: </th>
			<td><h:inputText value="#{ processamentoMatriculaDiscenteSerie.ano }" onkeyup="return formatarInteiro(this);" maxlength="4" size="4"/></td>
		</tr>
	
		<tr>
			<th>Curso: </th>
			<td>
				<h:selectOneMenu value="#{ processamentoMatriculaDiscenteSerie.curso.id }">
					<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					<f:selectItems value="#{ cursoMedio.allCombo }"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<c:if test="${processamentoMatriculaDiscenteSerie.processamento}">
				<c:set var="exibirApenasSenha" value="true" scope="request"/>
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
			</c:if>
			</td>
		</tr>
		
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{ processamentoMatriculaDiscenteSerie.confirmButton }" id="confirmar" action="#{ processamentoMatriculaDiscenteSerie.processar }"/>
				<h:commandButton value="Cancelar" action="#{ processamentoMatriculaDiscenteSerie.cancelar }" immediate="true"/>
			</td>
		</tr>
	</tfoot>
	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>