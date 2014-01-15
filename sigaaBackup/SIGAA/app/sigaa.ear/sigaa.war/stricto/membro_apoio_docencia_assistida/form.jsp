<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<f:view>
	<a4j:keepAlive beanName="membroApoioDocenciaAssistidaMBean"/>
	<h2 class="title"><ufrn:subSistema /> > Identificar Membro CATP</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<br/>
		<p>Informe o usuário que fará parte da Coordenação de Apoio Técnico-Pedagógico da Docência Assistida (CATP).</p>
	</div>

	<h:form id="form">

		<table class="formulario" width="60%">
			<caption class="formulario">Informe o Usuário</caption>
			<tr>
				<th class="obrigatorio">Usuário:</th>
				<td>
					<h:inputHidden id="idUsuario" value="#{membroApoioDocenciaAssistidaMBean.obj.usuario.id}"/>
					<h:inputText id="nomeUsuario" value="#{membroApoioDocenciaAssistidaMBean.obj.usuario.nome}" size="60" onkeyup="CAPS(this)" />

					<ajax:autocomplete source="form:nomeUsuario" target="form:idUsuario"
							baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
							indicator="indicatorUsuario" minimumCharacters="3"
							parser="new ResponseXmlToHtmlListParser()" parameters="servidor=true"/>

					<span id="indicatorUsuario" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					<ufrn:help><i>Apenas usuários com vínculo de Servidor serão listados</i></ufrn:help>
				</td>
			</tr>			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{membroApoioDocenciaAssistidaMBean.confirmButton}"	action="#{membroApoioDocenciaAssistidaMBean.cadastrar}" id="btncadastrar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{membroApoioDocenciaAssistidaMBean.cancelar}" immediate="true" id="btncancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
	
	<c:if test="${not empty membroApoioDocenciaAssistidaMBean.listagem}">	
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
			</div>
		</center>	
		
		<table class="listagem" style="width: 80%">
			<caption>Membros Cadastrados</caption>
			<thead>
			<tr>
				<th>Usuário</th>
				<th style="text-align: center;">Data Cadastro</th>
				<th width="1"></th>
			</tr>
			</thead>
			
			<tbody>
				<c:forEach items="#{membroApoioDocenciaAssistidaMBean.listagem}" var="item" varStatus="loop">		
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>
						${item.usuario.pessoa.nome}
					</td>
					<td style="text-align: center;">
						<ufrn:format type="dataHora" valor="${item.dataCadastro}"></ufrn:format>
					</td>
					<td>
						<h:commandLink action="#{membroApoioDocenciaAssistidaMBean.remover}" title="Remover" onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif"/>
							<f:setPropertyActionListener value="#{item}" target="#{membroApoioDocenciaAssistidaMBean.obj}"/>
						</h:commandLink>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		
		</table>
	</c:if>

	</h:form>
	<script type="text/javascript">$('form:nomeUsuario').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
