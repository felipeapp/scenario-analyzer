<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %>
<%@ taglib uri="/tags/sipac" prefix="sipac"  %>

<h2 class="tituloPagina">
	Alterar Dados Pessoais
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>
<c:if test="${acao == 'remover'}">
	<span class="subtitle">
		<fmt:message key="mensagem.confirma.remocao">
			<fmt:param value="da Pessoa"></fmt:param>
		</fmt:message>
	</span>
	<br>
	<br>
</c:if>

<style>
.areaDeDados  {
 width: 90%
}
.areaDeDados .dados .texto {
  margin-left: 14em;
}
</style>

<html:javascript staticJavascript="false" formName="pessoaForm" />

<html:form action="/geral/${acao}Pessoa" method="post" focus="nome" onsubmit="return validatePessoaForm(this);">
	<html:hidden property="id" />

	<div class="areaDeDados">
		<c:if test="${acao == 'alterar' || acao == 'criar'}">
			<h2>Dados Pessoais</h2>
        	<div class="dados">

            	<div class="head">Nome:</div>
            	<div class="texto">
                	<html:text property="nome" onkeyup="CAPS(this)" maxlength="100" size="73" value="${pessoa.nome}" />
	                <span class="required">&nbsp;</span><br/>
    	            <html:errors property="nome" />
        	    </div>

	            <div class="head">Data de Nascimento:</div>
    	        <div class="texto">
                	<sigaa:calendar name="pessoa" property="dataNascimento" />
	                <span class="required">&nbsp;</span>
    	            <html:errors property="dataNascimento" />
    	            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    	        	<b>Sexo: </b>
                	<html:radio name="pessoa" property="sexo" value="M" styleClass="noborder" styleId="sexoId">Masculino</html:radio>
					<html:radio name="pessoa" property="sexo" value="F" styleClass="noborder">Feminino</html:radio>
                	<span class="required">&nbsp;</span>
	                <html:errors property="sexo" />
				</div>

	            <div class="head">E-Mail:</div>
    	        <div class="texto">
                	<html:text property="email" maxlength="100" size="55" value="${pessoa.email}" />
    	            <html:errors property="email" />
				</div>

	            <div class="head">Nome da Mãe:</div>
    	        <div class="texto">
                	<html:text property="nomeMae" onkeyup="CAPS(this)" maxlength="100" size="73" value="${pessoa.nomeMae}" />
    	            <html:errors property="nomeMae" />
				</div>

	            <div class="head">Nome do Pai:</div>
    	        <div class="texto">
                	<html:text property="nomePai" onkeyup="CAPS(this)" maxlength="100" size="73" value="${pessoa.nomePai}" />
    	            <html:errors property="nomePai" />
				</div>

	            <div class="head">País Nacionalidade:</div>
    	        <div class="texto">
					<html:select property="paisNacionalidade" value="${pessoa.paisNacionalidade.id}" >
					<html:option value="">> Opções</html:option>
					</html:select>
					<html:errors property="paisNacionalidade" />&nbsp;&nbsp;&nbsp;&nbsp;
    	        	<b>UF: </b>
        	        <html:select property="ufNaturalidade" value="${pessoa.ufNaturalidade.id}" >
            	    <html:option value="">> Opções</html:option>
	                </html:select>
        	        <html:errors property="ufNaturalidade" />
				</div>

	            <div class="head">Naturalidade:</div>
    	        <div class="texto">
        	        <html:select property="municipioNaturalidade" value="${pessoa.municipioNaturalidade.id}" >
            	    <html:option value="">> Opções</html:option>
	                </html:select>
        	        <html:errors property="municipioNaturalidade" />&nbsp;&nbsp;&nbsp;&nbsp;
    	        	<b>Outro:</b>
                	<html:text property="municipioNaturalidade" onkeyup="CAPS(this)" maxlength="100" size="35" value="${pessoa.municipioNaturalidade}" />
    	            <html:errors property="municipios" />
            	</div>

	            <div class="head">Raça:</div>
    	        <div class="texto">
        	        <html:select property="tipoRaca" value="${pessoa.tipoRaca.id}" >
	            	    <html:option value="">> Opções</html:option>
	                	<html:options collection="tipoRacas" property="id" labelProperty="descricao"/>
	                </html:select>
        	        <html:errors property="tipoRaca" />&nbsp;&nbsp;&nbsp;&nbsp;
        	        <b>Estado Civil:</b>
         	        <html:select property="tipoEstadoCivil" value="${pessoa.tipoEstadoCivil.id}" >
            	    <html:option value="">> Opções</html:option>
                	<html:options collection="tipoEstadosCivis" property="id" labelProperty="descricao"/>
	                </html:select>
        	        <html:errors property="tipoEstadoCivil" />
            	</div>

	            <div class="head">Grau de Formação:</div>
    	        <div class="texto">
        	        <html:select property="tipoGrauFormacao" value="${pessoa.tipoGrauFormacao.id}" >
            	    <html:option value="">> Opções</html:option>
                	<html:options collection="tipoGrausFormacoes" property="id" labelProperty="descricao"/>
	                </html:select>
        	        <html:errors property="tipoGrauFormacao" />
            	</div>

	            <div class="head">Rede de Ensino:</div>
    	        <div class="texto">
        	        <html:select property="tipoRedeEnsino" value="${pessoa.tipoRedeEnsino.id}" >
            	    <html:option value="">> Opções</html:option>
                	<html:options collection="tipoRedesEnsinos" property="id" labelProperty="descricao"/>
	                </html:select>
        	        <html:errors property="tipoRedeEnsino" />
            	</div>

				<fieldset>
					<legend>Endereço Residencial</legend>
			        <div class="head">Logradouro:</div>
    		        <div class="texto">
	        	        <html:select property="tipoLogradouro" value="${pessoa.endereco.tipoLogradouro.id}" >
		            	    <html:option value="">> Selecione</html:option>
		            	    <html:options collection="tipoLogradouros" property="id" labelProperty="descricao"/>
		                </html:select>
            	    	<html:text property="logradouro" onkeyup="CAPS(this)" maxlength="100" size="55" value="${pessoa.enderecoResidencial.logradouro}" />
    	        	    <html:errors property="logradouro" />
					</div>

	    	        <div class="head">Número:</div>
    	    	    <div class="texto">
                		<html:text property="numero" maxlength="4" size="4" value="${pessoa.enderecoResidencial.numero}" /> &nbsp;&nbsp;
    		        	<b>Bairro:</b>
            	    	<html:text property="bairro" onkeyup="CAPS(this)" maxlength="100" size="25" value="${pessoa.enderecoResidencial.bairro}" /> &nbsp;&nbsp;
    		        	<b>CEP: </b>
        	        	<html:text property="cep" maxlength="9" size="9" onkeyup="formataCEP(this, event, null)" value="${pessoa.enderecoResidencial.cep}" />
    	    	        <html:errors property="cep" /> &nbsp;&nbsp;
    	        	    <html:errors property="bairro" /> &nbsp;&nbsp;
    	            	<html:errors property="numero" />
					</div>

		            <div class="head">Complemento:</div>
    		        <div class="texto">
        	        	<html:text property="complemento" onkeyup="CAPS(this)" maxlength="100" size="55" value="${pessoa.enderecoResidencial.complemento}" />
    	    	        <html:errors property="complemento" />
					</div>

		            <div class="head">Município:</div>
    		        <div class="texto">
    	    	        <html:errors property="municipio" />
					</div>

		            <div class="head">País:</div>
    		        <div class="texto">
						<html:select property="nomePais" value="${pessoa.enderecoResidencial.nomePais.id}" >
						<html:option value="">> Opções</html:option>
						</html:select>
						<html:errors property="nomePais" />
					</div>
				</fieldset>

				<br>
				<fieldset>
					<legend>Documentação</legend>

		            <div class="head">CPF:</div>
	    	        <div class="texto">
	                	<html:text property="cpf" maxlength="14" size="14" value="${pessoa.cpf}" onkeypress="formataCPF(this, event, null)" />
	                	  <sipac:help img="img/ajuda.gif">
					        <lu>
					         <li> O CPF deve ser digitado com 10 digitos sem pontos ou traços. <br>
					         <li> Os pontos e traços são inseridos automáticamente. <br>
					        </lu>
					      </sipac:help>
		                <span class="required">&nbsp;</span>
	    	            <html:errors property="cpf" />
					</div>

					<div class="head">Identidade:</div>
					<div class="texto">
						<table>
							<tr>
								<td>Órgão de Expedição:</td>
								<td><html:text property="orgaoExpedicao" maxlength="5" size="5" value="${pessoa.identidade.orgaoExpedicao}" />
    	    	        			<html:errors property="orgaoExpedicao" /></td>
    	    	        		<td>&nbsp;&nbsp;</td>
								<td>Data de Expedição:</td>
								<td><sigaa:calendar name="pessoa" property="dataExpedicao" />
    	            				<html:errors property="dataExpedicao" /></td>
							</tr>
							<tr>
								<td>Registro Geral (RG):</td>
								<td><html:text property="numero" maxlength="11" size="11" value="${pessoa.identidade.numero}" />
    	        	   				<html:errors property="numero" /></td>
							</tr>
						</table>
					</div>

		            <div class="head">Passaporte:</div>
	    	        <div class="texto">
	                	<html:text property="passaporte" maxlength="50" size="25" value="${pessoa.passaporte}" />
	    	            <html:errors property="passaporte" />
					</div>

		            <div class="head">Título Eleitoral:</div>
    		        <div class="texto" >
    		        	<table>
    		        		<tr>
    		        			<td>Número:</td>
    		        			<td><html:text property="numero" maxlength="15" size="13" value="${pessoa.tituloEleitor.numero}" />
    	    	        			<html:errors property="numero" /></td>
    	    	        		<td>&nbsp;&nbsp;</td>
    		        			<td>Expedição:</td>
    		        			<td><sigaa:calendar name="pessoa" property="dataExpedicao"/>
    	            				<html:errors property="dataExpedicao" /></td>
    		        		</tr>
    		        		<tr">
    		        			<td>Zona:</td>
    		        			<td><html:text property="zona" maxlength="5" size="3" value="${pessoa.tituloEleitor.zona}" />
    	    	        			<html:errors property="zona" /></td>
    	    	        		<td></td>
    		        			<td>Seção:</td>
    		        			<td><html:text property="secao" maxlength="3" size="3" value="${pessoa.tituloEleitor.secao}" />
    	    	        			<html:errors property="secao" /></td>
    		        		</tr>
    		        	</table>
					</div>

					<div class="head">Certificado Militar:</div>
					<div class="texto">
						<table >
							<tr>
								<td>Número:</td>
								<td><html:text property="numero" maxlength="15" size="15" value="${pessoa.certificadoMilitar.numero}" />
    	    	        			<html:errors property="numero" /></td>
     	    	        		<td>&nbsp;&nbsp;</td>
								<td>Série:</td>
								<td><html:text property="serie" maxlength="15" size="15" value="${pessoa.certificadoMilitar.serie}" />
    	    	       				<html:errors property="serie" /></td>
     	    	        		<td>&nbsp;&nbsp;</td>
								<td>Categoria:</td>
								<td><html:text property="categoria" maxlength="5" size="5" value="${pessoa.certificadoMilitar.categoria}" />
    	    	       				<html:errors property="categoria" /></td>
							</tr>
							<tr>
								<td>Data de Expedição:</td>
								<td><sigaa:calendar name="pessoa" property="dataExpedicao" />
    	            				<html:errors property="dataExpedicao" /></td>
     	    	        		<td>&nbsp;&nbsp;</td>
								<td>Órgão de Expedição:</td>
								<td><html:text property="orgaoExpedicao" maxlength="5" size="5" value="${pessoa.certificadoMilitar.orgaoExpedicao}" />
    	    	       				<html:errors property="orgaoExpedicao" /></td>
     	    	        		<td>&nbsp;&nbsp;</td>
								<td></td>
							</tr>
						</table>
					</div>

				</fieldset>
			</div>
		</c:if>

    	<c:if test="${acao == 'remover'}">
    	    <h2>Dados da Pessoa</h2>
        	<div class="dados">
            	<div class="head">Nome:</div>
	            <div class="texto">${pessoa.nome}</div>

            	<div class="head">CPF:</div>
	            <div class="texto">${pessoa.cpf}</div>

	            <div class="head">Sexo:</div>
    	        <div class="texto">
					<c:choose>
						<c:when test="${pessoa.sexo == M}">Masculino</c:when>
						<c:otherwise>Feminino</c:otherwise>
					</c:choose>
    	        </div>

            	<div class="head">Data de Nascimento:</div>
	            <div class="texto"><fmt:formatDate value="${pessoa.dataNascimento}" pattern="dd/MM/yyyy"/></div>

	            <div class="head">Nome da Mãe:</div>
    	        <div class="texto">${pessoa.nomeMae}</div>

	            <div class="head">Nome do Pai:</div>
    	        <div class="texto">${pessoa.nomePai}</div>

	            <div class="head">E-mail:</div>
    	        <div class="texto">${pessoa.email}</div>

	            <div class="head">Passaporte:</div>
    	        <div class="texto">${pessoa.passaporte}</div>

	            <div class="head">Abreviatura:</div>
    	        <div class="texto">${pessoa.abreviatura}</div>

	            <div class="head">Estado Civil:</div>
    	        <div class="texto">${pessoa.tipoEstadoCivil}</div>

	            <div class="head">Naturalidade:</div>
    	        <div class="texto">${pessoa.municipioNaturalidade}</div>

	            <div class="head">UF da Naturalidade:</div>
    	        <div class="texto">${pessoa.ufNaturalidade.descricao}</div>

	            <div class="head">País Nacionalidade:</div>
    	        <div class="texto">${pessoa.paisNacionalidade.nomePais}</div>

	            <div class="head">Grau de Formação:</div>
    	        <div class="texto">${pessoa.tipoGrauFormacao.descricao}</div>

	            <div class="head">Rede de Ensino:</div>
    	        <div class="texto">${pessoa.tipoRedeEnsino.descricao}</div>

	            <div class="head">Raça:</div>
    	        <div class="texto">${pessoa.tipoRaca.descricao}</div>

				<div class="areaDeDados2">
				<h2>Endereco Residêncial:</h2>
    	    	<div class="dados">

		            <div class="head">CEP:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.cep}</div>

			        <div class="head">Logradouro:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.logradouro}</div>

	    	        <div class="head">Número:</div>
    	    	    <div class="texto">${pessoa.enderecoResidencial.numero}</div>

		            <div class="head">Complemento:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.complemento}</div>

		            <div class="head">Bairro:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.bairro}</div>

		            <div class="head">Município:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.municipio}</div>

		            <div class="head">País:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.nomePais.id}</div>

	        	    <div class="head">Correspondência:</div>
    	        	<div class="texto">${pessoa.enderecoResidencial.correspondencia}</div>

		            <div class="head">Caixa Postal:</div>
    		        <div class="texto">${pessoa.enderecoResidencial.caixaPostal}</div>
				</div>
				</div>

				<div class="areaDeDados2">
				<h2>Título de Eleitor:</h2>
    	    	<div class="dados">
		            <div class="head">Número:</div>
    		        <div class="texto">${pessoa.tituloEleitor.numero}</div>

		            <div class="head">Zona:</div>
    		        <div class="texto">${pessoa.tituloEleitor.zona}</div>

		            <div class="head">Seção:</div>
    		        <div class="texto">${pessoa.tituloEleitor.secao}</div>

		            <div class="head">Data de Expedição:</div>
    		        <div class="texto"><fmt:formatDate value="${pessoa.tituloEleitor.dataExpedicao}" pattern="dd/MM/yyyy"/></div>

				</div>
				</div>

				<div class="areaDeDados2">
				<h2>Certificado Militar:</h2>
    	    	<div class="dados">
		            <div class="head">Número:</div>
    		        <div class="texto">${pessoa.certificadoMilitar.numero}</div>

		            <div class="head">Série:</div>
    		        <div class="texto">${pessoa.certificadoMilitar.serie}</div>

		            <div class="head">Categoria:</div>
    		        <div class="texto">${pessoa.certificadoMilitar.categoria}" /></div>

		            <div class="head">Data de Expedição:</div>
    		        <div class="texto"><fmt:formatDate value="${pessoa.certificadoMilitar.dataExpedicao}" pattern='dd/MM/yyyy'/></div>

		            <div class="head">Órgão de Expedição:</div>
    		        <div class="texto">${pessoa.certificadoMilitar.orgaoExpedicao}</div>
				</div>
				</div>

				<div class="areaDeDados2">
				<h2>Identidade:</h2>
    	    	<div class="dados">
		            <div class="head">Número:</div>
    		        <div class="texto">${pessoa.identidade.numero}</div>

         			<div class="head">Órgão de Expedição:</div>
    		        <div class="texto">${pessoa.identidade.orgaoExpedicao}</div>

		            <div class="head">Data de Expedição:</div>
    		        <div class="texto"><fmt:formatDate value="${pessoa.identidade.dataExpedicao}" pattern='dd/MM/yyyy'/></div>

				</div>
				</div>
 	        </div>
	    </c:if>
	<br>
	</div>
	    <br>
		<c:if test="${(acao == 'criar') or (acao == 'alterar')}">
			<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		</c:if>
		<br><br>
		<div class="botoes">
			<c:choose>
				<c:when test="${acao == 'criar' or (acao == 'alterar' and param.origem=='consulta')}">
					<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuEnsinoGraduacao.do'"><fmt:message key="botao.cancelar" /></html:button>
				</c:when>
				<c:otherwise>
					<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/geral/listarPessoas.do'"><fmt:message key="botao.cancelar" /></html:button>
				</c:otherwise>
			</c:choose>
			<html:submit><fmt:message key="botao.${acao}" /></html:submit>
					</div>


</html:form>

<br>
<center>
	<html:link action="/verMenuEnsino">Menu do Ensino</html:link>
</center>