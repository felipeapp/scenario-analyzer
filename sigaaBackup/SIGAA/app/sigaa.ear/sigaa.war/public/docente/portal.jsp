<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<div id="perfil-docente">
	<h4>Perfil Pessoal</h4>

	<c:choose>
		<c:when test="${not empty portal.perfil}">
			<dl>
				<dt> Descri��o pessoal </dt>
				<dd>
					<c:choose>
						<c:when test="${not empty portal.perfil.descricao}">
							<ufrn:format type="texto" valor="${portal.perfil.descricao}"/> 
						</c:when>
						<c:otherwise>
							<i> n�o informada </i>
						</c:otherwise>
					</c:choose> 
				</dd>
			</dl>
			<dl>
				<dt> Forma��o acad�mica/profissional (Onde obteve os t�tulos, atua��o profissional, etc.) </dt>
				<dd> 
					<c:choose>
						<c:when test="${not empty portal.perfil.formacao}">
							<ufrn:format type="texto" valor="${portal.perfil.formacao}"/> 
						</c:when>
						<c:otherwise>
							<i> n�o informada </i>
						</c:otherwise>
					</c:choose> 					
				</dd>
			</dl>
			<dl>
				<dt> 
					�reas de Interesse 
					<span class="info">	(�reas de interesse de ensino e pesquisa) </span>
				</dt>
				<dd> 
					<c:choose>
						<c:when test="${not empty portal.perfil.areas}">
							<ufrn:format type="texto" valor="${portal.perfil.areas}"/> 
						</c:when>
						<c:otherwise>
							<i> n�o informadas </i>
						</c:otherwise>
					</c:choose> 				
				</dd>
			</dl>
			<dl>
				<dt> Curr�culo Lattes: </dt>
				<dd>
					<c:choose>
						<c:when test="${not empty portal.perfil.enderecoLattes}">
							<a href="${ portal.perfil.enderecoLattes}" target="_blank" id="endereco-lattes"> 
								${portal.perfil.enderecoLattes} 
							</a> 
						</c:when>
						<c:otherwise>
							<i> link n�o informado </i>
						</c:otherwise>
					</c:choose> 
				</dd>
			</dl>
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Perfil pessoal n�o cadastrado
			</p>
		</c:otherwise>
	</c:choose>
</div>

<div id="formacao-academica">
	<h4>Forma��o Acad�mica</h4>

	<c:if test="${not empty portal.formacoes}">
	<dl>
		<c:forEach var="formacao" items="#{portal.formacoes}">
		<dt>
			<span class="ano"><ufrn:format type="ano" name="formacao" property="fim"/></span> - ${formacao.formacao}
		</dt>
		<dd>
			${formacao.grau} <br />
			${formacao.instituicao} <br />
		</dd>
		</c:forEach>
	</dl>
	</c:if>

	<c:if test="${empty portal.formacoes}">
		<p class="vazio">
			Forma��o acad�mica n�o cadastrada
		</p>
	</c:if>
</div>

<c:if test="${not empty portal.usuario}">
<div id="contato">
	<h4>Contatos</h4>
	<dl>
		<dt> Endere�o profissional </dt>
		<dd> 
			<c:choose>
				<c:when test="${not empty portal.perfil.endereco}">
					<ufrn:format type="texto" valor="${portal.perfil.endereco}"/><br />
				</c:when>
				<c:otherwise>
					<i> n�o informado </i><br />
				</c:otherwise>
			</c:choose> 
		</dd>
	</dl>
	<dl>				
		<dt> Sala </dt>
		<dd> 
			<c:choose>
				<c:when test="${not empty portal.perfil.sala}">
					<ufrn:format type="texto" valor="${portal.perfil.sala}"/>
				</c:when>
				<c:otherwise>
					<i> n�o informado </i>
				</c:otherwise>
			</c:choose> 	
		</dd>
	</dl>
	<dl>				
		<dt> Telefone/Ramal </dt>
		<dd> 
			<c:choose>
				<c:when test="${not empty portal.usuario.ramal}">
					<ufrn:format type="texto" valor="${portal.usuario.ramal}"/>
				</c:when>
				<c:otherwise>
					<i> n�o informado </i>
				</c:otherwise>
			</c:choose> 	
		</dd>
	</dl>
	<dl>
		<dt> Endere�o eletr�nico </dt>
		<dd> 
			<c:choose>
				<c:when test="${not portal.perfil.ocultarEmail && not empty portal.usuario.email}">
				<ufrn:format type="texto" valor="${portal.usuario.email}"/> 
				</c:when>
				<c:otherwise>
					<i> n�o informado </i>
				</c:otherwise>
			</c:choose> 	
		</dd>
	</dl>
</div>
</c:if>

</f:view>
<%@include file="/public/include/rodape.jsp" %>
