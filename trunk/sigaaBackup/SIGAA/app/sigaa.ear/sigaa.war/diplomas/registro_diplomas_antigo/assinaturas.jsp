<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Registro de Diplomas Antigo </h2>

<h:form id="form">
	
<div class="descricaoOperacao">
<p><b>Caro Usuário,</b></p>
	<p>
		Selecione, dentre as opções abaixo, aqueles que assinavam o diploma.
		Não havendo registro ainda, informe	os nomes e respectivas funções que constam no diploma.
	</p>
</div>

<div class="infoAltRem">
	<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Selecionar Assinaturas
</div>
<table class="formulario" width="80%">
	<caption>Assinaturas no Diploma Registrado</caption>
	<thead>
		<tr>
			<th width="45%">Função</th>
			<th>Nome</th>
			<th width="5%"></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="item" items="#{ responsavelAssinaturaDiplomasBean.all }" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<th valign="top" class="rotulo">${item.descricaoFuncaoReitor}:</th><td> ${item.nomeReitor}</td>
				<td style="text-align: left;" valign="top" rowspan="3">
					<h:commandLink action="#{ registroDiplomaAntigoMBean.escolherAssinatura }">
						<f:param name="id" value="#{ item.id }" />
						<f:verbatim>
							<h:graphicImage value="/img/seta.gif"style="overflow: visible;" title="Selecionar Assinaturas"/>
						</f:verbatim>
					</h:commandLink> 
				</td>
			</tr>
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<th class="rotulo">${item.descricaoFuncaoDiretorUnidadeDiplomas}:</th><td>${item.nomeDiretorUnidadeDiplomas}</td>
			</tr>
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<th class="rotulo">${item.descricaoFuncaoDiretorGraduacao}:</th><td>${item.nomeDiretorGraduacao}</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="3" class="subFormulario">OUTRO (Informe os Nomes e Respectivas Funções)</td>
		</tr>
		<tr>
			<td colspan="3" class="subFormulario">Reitor da Instituição</td>
		</tr>
		<tr>
			<th class="required">Nome: </th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.nomeReitor}" id="nomeReitor" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th class="required">Descrição da Função: </th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.descricaoFuncaoReitor}" id="funcaoReitor" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th>Gênero: </th>
			<td>
				<h:selectOneRadio value="#{registroDiplomaAntigoMBean.assinaturaDiploma.generoReitor }" id="generoReitor">
					<f:selectItems value="#{ registroDiplomaAntigoMBean.mascFem }"/>
				</h:selectOneRadio>
			</td>
			<td></td>
		</tr>
		<tr>
			<td colspan="3" class="subFormulario">Diretor da Unidade de Registro de Diplomas</td>
		</tr>
		<tr>
			<th class="required">Nome: </th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.nomeDiretorUnidadeDiplomas}" id="nomeDiretorDred" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th class="required">Descrição da Função: </th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.descricaoFuncaoDiretorUnidadeDiplomas}" id="funcaoDiretorDred" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th>Gênero: </th>
			<td>
				<h:selectOneRadio value="#{registroDiplomaAntigoMBean.assinaturaDiploma.generoDiretorUnidadeDiplomas }" id="generoDiretorUnidadeDiplomas">
					<f:selectItems value="#{ registroDiplomaAntigoMBean.mascFem }"/>
				</h:selectOneRadio>
			</td>
			<td></td>
		</tr>
		<tr>
			<td colspan="3" class="subFormulario">Responsável pela Graduação na Instituição</td>
		</tr>
		<tr>
			<th class="required">Nome:</th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.nomeDiretorGraduacao}" id="nomeDiretorDae" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th class="required">Descrição da Função:</th>
			<td>
				<h:inputText value="#{registroDiplomaAntigoMBean.assinaturaDiploma.descricaoFuncaoDiretorGraduacao}" id="funcaoDiretorDae" size="70" maxlength="120"/>
			</td>
			<td></td>
		</tr>
		<tr>
			<th>Gênero: </th>
			<td>
				<h:selectOneRadio value="#{registroDiplomaAntigoMBean.assinaturaDiploma.generoDiretorGraduacao }" id="generoDiretorGraduacao">
					<f:selectItems value="#{ registroDiplomaAntigoMBean.mascFem }"/>
				</h:selectOneRadio>
			</td>
			<td></td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton action="#{registroDiplomaAntigoMBean.formRegistroAntigo}" value="<< Voltar" id="voltar"/>
				<h:commandButton action="#{registroDiplomaAntigoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
				<h:commandButton action="#{registroDiplomaAntigoMBean.submeterAssinaturas}" value="Próximo Passo >>" id="proximo"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>