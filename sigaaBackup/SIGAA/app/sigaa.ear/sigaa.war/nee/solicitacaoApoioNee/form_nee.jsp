<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	table.formulario th{ font-weight: bold; }
	#noOutPut{ font-weight: normal;}
	#visualiza_on {display : block; vertical-align: middle;}
	#visualiza_off {display : none}
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2><ufrn:subSistema /> > Cadastro de Discente com NEE</h2>
	<h:outputText value="#{solicitacaoApoioNee.create}" />
	
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Discente com Necessidades Educacionais especiais</caption>
			<tbody>
				<tr>
					<th>Aluno: </th>
					<td width="20%">
						<h:outputText value="#{solicitacaoApoioNee.obj.discente.nome}" id="nomeDiscente"/>
					</td>
					<th>Data de Nascimento: </th>
					<td>
						<h:outputText value="#{solicitacaoApoioNee.obj.discente.pessoa.dataNascimento}" id="dataNascimentoDiscente"/>
					</td>
				</tr>
				<tr>	
					<th>Matrícula:</th>
					<td>
						<h:outputText value="#{solicitacaoApoioNee.obj.discente.matricula}" id="matriculaDiscente"/>
					</td>
					<th>Sexo:</th>
					<td>
						<h:outputText value="#{solicitacaoApoioNee.obj.discente.pessoa.sexo eq 'F' ? 'Feminino' : 'Masculino'}" id="sexoDiscente"/>
					</td>
				</tr>
				<c:choose>
					<c:when test="${solicitacaoApoioNee.obj.discente.graduacao}">
						<tr>
							<th> Curso: </th>
							<td colspan="4"> ${solicitacaoApoioNee.obj.discente.matrizCurricular.descricao}</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<th> Curso: </th>
							<td colspan="4"> ${solicitacaoApoioNee.obj.discente.curso.descricao}</td>
						</tr>
					</c:otherwise>
				</c:choose>
				<tr>
					<th width="22%"> Forma de Ingresso: </th>
					<td> ${solicitacaoApoioNee.obj.discente.formaIngresso.descricao}</td>
				</tr>
				<tr>	
					<th> Ano/Período de Ingresso: </th>
					<td >${solicitacaoApoioNee.obj.discente.anoIngresso}.${solicitacaoApoioNee.obj.discente.periodoIngresso}</td>
					<th> Ano/Período Atual: </th>
					<td >${solicitacaoApoioNee.calendarioVigente.ano}.${solicitacaoApoioNee.periodoAtual}</td>
				</tr>
				
				<tr>
					<th>Endereço: </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.tipoLogradouro}
						${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.logradouro},
						nº ${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.numero}
					</td>
				</tr>
				<tr>	
					<th> Bairro: </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.bairro}
					</td>
					<th>Cidade: </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.municipio.nome} / ${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.unidadeFederativa.sigla}
					</td>
				</tr>
				
				<tr>
					<th>CEP: </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.enderecoContato.cep}
					</td>
					<th>Telefone(s): </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.telefone} 
						<c:if test="${solicitacaoApoioNee.obj.discente.pessoa.telefone != '' and solicitacaoApoioNee.obj.discente.pessoa.celular != ''}">/</c:if> 
						${solicitacaoApoioNee.obj.discente.pessoa.celular}
					</td>
				</tr>
				
				<tr>
					<th>E-mail: </th>
					<td>
						${solicitacaoApoioNee.obj.discente.pessoa.email}
					</td>
					<th>Data de Solicitação: </th>
					<td>
						<h:outputText value="#{solicitacaoApoioNee.obj.dataCadastro}">
							<f:convertDateTime pattern="dd/MM/yyyy"/>
						</h:outputText>
					</td>
				</tr>
				
				<tr>
					<th colspan="1" id="noOutPut" class="obrigatorio">Tipo de Necessidade Educacional Especial: </th>
					<td colspan="2">
						<t:selectManyCheckbox value="#{solicitacaoApoioNee.tiposNecessidadesEspeciaisSelecionadas}" id="opcaoTipo" layout="pageDirection" layoutWidth="3">
							<f:selectItems value="#{tipoNecessidadeEspecial.allCombo}"/>
						</t:selectManyCheckbox>
					</td>
				</tr>
			
				<tr>
					<th colspan="1" class="visualiza_${solicitacaoApoioNee.parecerNeeClass}" id="noOutPut">
						<h:outputText rendered="#{solicitacaoApoioNee.parecerNee}" styleClass="obrigatorio">Parecer:</h:outputText>
					</th>
					<td colspan="5">
						<h:selectOneRadio value="#{solicitacaoApoioNee.obj.parecerAtivo}" id="opcaoParecerAtivo" 
							layout="pageDirection" rendered="#{solicitacaoApoioNee.parecerNee}"
							valueChangeListener="#{solicitacaoApoioNee.selecionarParecer}">
					 		<a4j:support event="onchange" reRender="opcaoStatus"/>
							<f:selectItem itemLabel="DEFERIDO" itemValue="true" />
							<f:selectItem itemLabel="INDEFERIDO" itemValue="false" />
						</h:selectOneRadio>
					</td>
				</tr>
				
				<tr>
					<th colspan="1">
						<h:outputText value="Situação do Atendimento:" styleClass="obrigatorio" rendered="#{solicitacaoApoioNee.parecerNee}" style="font-weight: normal;"/>
						<h:outputText value="Situação do Atendimento:" rendered="#{!solicitacaoApoioNee.parecerNee && solicitacaoApoioNee.obj.id > 0}"/>
					</th>
					<td colspan="5">
						<t:selectOneRadio value="#{solicitacaoApoioNee.obj.statusAtendimento.id}" id="opcaoStatus" 
							layout="lineDirection" rendered="#{solicitacaoApoioNee.parecerNee}" disabled="#{solicitacaoApoioNee.situacaoAtendimento}">
							<f:selectItems value="#{statusAtendimento.allCombo}" />
						</t:selectOneRadio>
						<h:outputText value="#{solicitacaoApoioNee.obj.statusAtendimento.denominacao}" id="statusAtendimento" rendered="#{!solicitacaoApoioNee.parecerNee}" />
					</td>
				</tr>
				
				<tr>
					<th colspan="1" id="noOutPut" class="${!solicitacaoApoioNee.parecerNee ? 'obrigatorio' : ''}">
						Justificativa para solicitação de apoio a CAENE:
					</th>
					<td colspan="3">
						<h:inputTextarea value="#{solicitacaoApoioNee.obj.justificativaSolicitacao}" id="justificativa" rows="4" cols="80" readonly="#{solicitacaoApoioNee.parecerNee}" />
					</td>
				</tr>	
				
				<tr>
					<th colspan="1" class="visualiza_${solicitacaoApoioNee.parecerNeeClass}" id="noOutPut">
						<h:outputText rendered="#{solicitacaoApoioNee.parecerNee}" styleClass="obrigatorio">Parecer Técnico da CAENE:</h:outputText></th>
					<td colspan="3">
						<h:inputTextarea value="#{solicitacaoApoioNee.obj.parecerComissao}" id="parecerComissao" rows="4" cols="80" rendered="#{solicitacaoApoioNee.parecerNee}"/>
					</td>
				</tr>	
			
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{solicitacaoApoioNee.confirmButton}" id="btnCadastrar" action="#{solicitacaoApoioNee.cadastrar}" /> 
						<h:commandButton value="Cancelar" id="btnCancelar" action="#{solicitacaoApoioNee.cancelarCadastro}" onclick="#{confirm}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>				
	
</f:view>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>